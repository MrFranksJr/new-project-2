package com.gaminggaiden.rebirth

import com.gaminggaiden.rebirth.application.usecases.GetGames
import com.gaminggaiden.rebirth.application.usecases.GetSummary
import com.gaminggaiden.rebirth.infrastructure.api.ktor.startKtorServer
import com.gaminggaiden.rebirth.infrastructure.persistence.sqlite.*
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

    // 2. Initialize Repositories
    val gameRepository = SqliteGameRepository()
    val pcRepository = SqliteGamingPCRepository()

    // 3. Initialize Use Cases
    val getGamesUseCase = GetGames(gameRepository)
    val getSummaryUseCase = GetSummary(gameRepository, pcRepository)

    // 4. Start Ktor Server
    println("Starting Gaming Gaiden Rebirth API on port 8080...")
    startKtorServer(
        getGamesUseCase = getGamesUseCase,
        getSummaryUseCase = getSummaryUseCase,
        port = 8080,
        wait = true
    )
}
