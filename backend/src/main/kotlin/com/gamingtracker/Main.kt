package com.gamingtracker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import com.gamingtracker.application.ports.output.AutostartPort
import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.ports.output.ProcessMonitor
import com.gamingtracker.application.services.TrackingService
import com.gamingtracker.application.usecases.*
import com.gamingtracker.infrastructure.api.ktor.startKtorServer
import com.gamingtracker.infrastructure.persistence.sqlite.*
import com.gamingtracker.infrastructure.system.StaticVersionProvider
import com.gamingtracker.infrastructure.system.windows.WindowsAutostartManager
import com.gamingtracker.infrastructure.system.windows.WindowsCleanupManager
import com.gamingtracker.infrastructure.system.windows.WindowsProcessMonitor
import com.gamingtracker.infrastructure.system.windows.WindowsRegistryStatsProvider
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.awt.Desktop
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

fun main() {
    // 1. Initialize Database
    println("Initializing Database...")
    val mainDb = Database.connect("jdbc:sqlite:gaming-tracker.db", driver = "org.sqlite.JDBC")
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

    // 3. Initialize Legacy Adapter (restoring mainDb as default to avoid hijacking)
    val legacyDatabaseAdapter = SqliteLegacyDatabaseAdapter("GamingGaiden.db")
    TransactionManager.defaultDatabase = mainDb

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

    // Wait for server to be ready
    println("Waiting for server to be ready (checking /api/summary and /)...")
    var isReady = false
    var attempts = 0
    while (!isReady && attempts < 50) {
        try {
            val apiConnection = URL("http://127.0.0.1:8080/api/summary").openConnection() as HttpURLConnection
            apiConnection.requestMethod = "GET"
            apiConnection.connectTimeout = 500
            apiConnection.readTimeout = 500
            
            val indexConnection = URL("http://127.0.0.1:8080/").openConnection() as HttpURLConnection
            indexConnection.requestMethod = "GET"
            indexConnection.connectTimeout = 500
            indexConnection.readTimeout = 500
            
            if (apiConnection.responseCode == 200 && indexConnection.responseCode == 200) {
                isReady = true
                println("Server is ready and serving index.html!")
            }
        } catch (e: Exception) {
            attempts++
            Thread.sleep(200)
        }
    }
    if (!isReady) {
        println("Warning: Server not ready after 10 seconds. Proceeding anyway...")
    }

    // 5. Start Compose UI (Tray only)
    application {
        Tray(
            icon = ColorPainter(Color(0xFF1976D2)),
            menu = {
                Item("Open Summary") {
                    openBrowser("http://127.0.0.1:8080/#summary")
                }
                Item("Games") {
                    openBrowser("http://127.0.0.1:8080/#games")
                }
                Item("Add Game") {
                    openBrowser("http://127.0.0.1:8080/#add")
                }
                Item("Uninstall") {
                    openBrowser("http://127.0.0.1:8080/#uninstall")
                }
                Item("Exit") {
                    exitApplication()
                }
            }
        )
    }
}

private fun openBrowser(url: String) {
    try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            // Fallback for Windows if Desktop API fails
            val runtime = Runtime.getRuntime()
            runtime.exec("rundll32 url.dll,FileProtocolHandler $url")
        }
    } catch (e: Exception) {
        println("Error opening browser: ${e.message}")
    }
}
