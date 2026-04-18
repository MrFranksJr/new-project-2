package com.gamingtracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gamingtracker.application.ports.output.ProcessMonitor
import com.gamingtracker.application.services.TrackingService
import com.gamingtracker.application.usecases.*
import com.gamingtracker.infrastructure.api.ktor.startKtorServer
import com.gamingtracker.infrastructure.persistence.sqlite.*
import com.gamingtracker.infrastructure.system.StaticVersionProvider
import com.gamingtracker.infrastructure.system.windows.WindowsProcessMonitor
import com.gamingtracker.infrastructure.system.windows.WindowsRegistryStatsProvider
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
        port = 8080,
        wait = false
    )

    // 5. Start Compose UI
    application {
        Window(onCloseRequest = ::exitApplication, title = "Gaming Tracker") {
            val state = rememberWebViewState("http://localhost:8080")
            WebView(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
