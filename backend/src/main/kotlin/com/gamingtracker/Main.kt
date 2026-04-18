package com.gamingtracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gamingtracker.application.ports.output.ProcessMonitor
import com.gamingtracker.application.ports.output.AutostartPort
import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.services.TrackingService
import com.gamingtracker.application.usecases.*
import com.gamingtracker.infrastructure.api.ktor.startKtorServer
import com.gamingtracker.infrastructure.persistence.sqlite.*
import com.gamingtracker.infrastructure.system.StaticVersionProvider
import com.gamingtracker.infrastructure.system.windows.WindowsCleanupManager
import com.gamingtracker.infrastructure.system.windows.WindowsProcessMonitor
import com.gamingtracker.infrastructure.system.windows.WindowsAutostartManager
import com.gamingtracker.infrastructure.system.windows.WindowsRegistryStatsProvider
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.window.Tray
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main() {
    // 1. Initialize Database
    println("Initializing Database...")
    Database.connect("jdbc:sqlite:gaming-tracker.db")
    transaction {
        SchemaUtils.create(GamesTable, GamingSessionsTable, GamingPCsTable, GameGamingPCsTable)
    }

    // 2. Initialize Repositories and Infrastructure
    val gameRepository = SqliteGameRepository()
    val pcRepository = SqliteGamingPCRepository()
    val sessionRepository = SqliteSessionRepository()
    val statsProvider = WindowsRegistryStatsProvider()
    val processMonitor = if (System.getProperty("os.name").contains("Windows")) {
        WindowsProcessMonitor()
    } else {
        object : ProcessMonitor {
            override fun getRunningExecutables(): List<String> = emptyList()
        }
    }

    val autostartPort: AutostartPort = if (System.getProperty("os.name").contains("Windows")) {
        WindowsAutostartManager()
    } else {
        object : AutostartPort {
            override fun isEnabled(): Boolean = false
            override fun setEnabled(enabled: Boolean) {}
        }
    }

    val toggleAutostartUseCase = ToggleAutostart(autostartPort)

    val getAutostartStatusUseCase = GetAutostartStatus(autostartPort)

    val cleanupPort: CleanupPort = if (System.getProperty("os.name").contains("Windows")) {
        WindowsCleanupManager("gaming-tracker.db")
    } else {
        object : CleanupPort {
            override fun performCleanup(deleteDb: Boolean) = com.gamingtracker.application.ports.output.CleanupResult(
                autostartRemoved = false,
                dbDeleted = deleteDb && File("gaming-tracker.db").delete()
            )
        }
    }

    val performCleanupUseCase = PerformCleanup(cleanupPort)

    if (!autostartPort.isEnabled()) {
        autostartPort.setEnabled(true)
    }

    // Legacy database will be connected within the use case when needed
    val legacyDatabaseAdapter = SqliteLegacyDatabaseAdapter("GamingGaiden.db")

    // 3. Initialize Use Cases
    val getGamesUseCase = GetGames(gameRepository)
    val getSummaryUseCase = GetSummary(gameRepository, pcRepository, statsProvider)
    val trackGameSessionUseCase = TrackGameSession(gameRepository, sessionRepository, pcRepository)
    val migrateLegacyDataUseCase = MigrateLegacyData(gameRepository, sessionRepository, legacyDatabaseAdapter)
    val addGameUseCase = AddGame(gameRepository)
    val getUpdateStatusUseCase = CheckForUpdates(
        versionProvider = StaticVersionProvider(latestVersion = "1.0.1"),
        currentVersion = "1.0.0"
    )
    
    val trackingService = TrackingService(
        gameRepository, 
        processMonitor, 
        trackGameSessionUseCase
    )
    trackingService.startTracking()

    // 4. Start Ktor Server
    println("Starting Gaming Tracker API on port 8080...")
    startKtorServer(
        getGamesUseCase = getGamesUseCase,
        getSummaryUseCase = getSummaryUseCase,
        migrateLegacyDataUseCase = migrateLegacyDataUseCase,
        addGameUseCase = addGameUseCase,
        getUpdateStatusUseCase = getUpdateStatusUseCase,
        toggleAutostartUseCase = toggleAutostartUseCase,
        getAutostartStatusUseCase = getAutostartStatusUseCase,
        performCleanupUseCase = performCleanupUseCase,
        port = 8080,
        wait = false
    )

    // 5. Start Compose UI
    @OptIn(ExperimentalComposeUiApi::class)
    application {
        var url by remember { mutableStateOf("http://localhost:8080") }
        var isWindowVisible by remember { mutableStateOf(true) }
        Tray(
            icon = ColorPainter(Color(0xFF1976D2)),
            menu = {
                Item("Open Summary") {
                    isWindowVisible = true
                    url = "http://localhost:8080/#summary"
                }
                Item("Games") {
                    isWindowVisible = true
                    url = "http://localhost:8080/#games"
                }
                Item("Add Game") {
                    isWindowVisible = true
                    url = "http://localhost:8080/#add"
                }
                Item("Uninstall") {
                    isWindowVisible = true
                    url = "http://localhost:8080/#uninstall"
                }
                Item("Exit") {
                    exitApplication()
                }
            }
        )
        if (isWindowVisible) {
            Window(
                onCloseRequest = { isWindowVisible = false },
                title = "Gaming Tracker"
            ) {
                val state = rememberWebViewState(url = url)
                WebView(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
