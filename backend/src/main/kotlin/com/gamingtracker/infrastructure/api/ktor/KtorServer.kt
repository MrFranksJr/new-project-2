package com.gamingtracker.infrastructure.api.ktor

import com.gamingtracker.application.ports.input.AddGameUseCase
import com.gamingtracker.application.ports.input.GetGamesUseCase
import com.gamingtracker.application.ports.input.GetSummaryUseCase
import com.gamingtracker.application.ports.input.GetUpdateStatusUseCase
import com.gamingtracker.application.ports.input.MigrateLegacyDataUseCase
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

fun startKtorServer(
    getGamesUseCase: GetGamesUseCase,
    getSummaryUseCase: GetSummaryUseCase,
    migrateLegacyDataUseCase: MigrateLegacyDataUseCase,
    addGameUseCase: AddGameUseCase,
    getUpdateStatusUseCase: GetUpdateStatusUseCase,
    port: Int = 8080,
    wait: Boolean = false
) {
    embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
        }
        configureRouting(getGamesUseCase, getSummaryUseCase, migrateLegacyDataUseCase, addGameUseCase, getUpdateStatusUseCase)
    }.start(wait = wait)
}
