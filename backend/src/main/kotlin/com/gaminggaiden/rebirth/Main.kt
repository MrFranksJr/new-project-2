package com.gaminggaiden.rebirth

import com.gaminggaiden.rebirth.application.usecases.GetGames
import com.gaminggaiden.rebirth.application.usecases.GetSummary
import com.gaminggaiden.rebirth.application.usecases.MigrateLegacyData
import com.gaminggaiden.rebirth.application.usecases.TrackGameSession
import com.gaminggaiden.rebirth.infrastructure.api.ktor.startKtorServer
import com.gaminggaiden.rebirth.infrastructure.persistence.sqlite.*
import com.gaminggaiden.rebirth.infrastructure.system.windows.WindowsRegistryStatsProvider
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
    // Legacy database will be connected within the use case when needed
    val legacyDatabaseAdapter = SqliteLegacyDatabaseAdapter("GamingGaiden.db")

    // 3. Initialize Use Cases
    val getGamesUseCase = GetGames(gameRepository)
    val getSummaryUseCase = GetSummary(gameRepository, pcRepository, statsProvider)
    val trackGameSessionUseCase = TrackGameSession(gameRepository, sessionRepository, pcRepository)
    val migrateLegacyDataUseCase = MigrateLegacyData(gameRepository, sessionRepository, legacyDatabaseAdapter)

    // 4. Start Ktor Server
    println("Starting Gaming Gaiden Rebirth API on port 8080...")
    startKtorServer(
        getGamesUseCase = getGamesUseCase,
        getSummaryUseCase = getSummaryUseCase,
        migrateLegacyDataUseCase = migrateLegacyDataUseCase,
        port = 8080,
        wait = true
    )
}
