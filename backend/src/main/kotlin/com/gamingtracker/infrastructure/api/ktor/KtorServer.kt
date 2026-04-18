package com.gamingtracker.infrastructure.api.ktor

import com.gamingtracker.application.ports.input.*
import com.gamingtracker.application.usecases.GetAutostartStatus
import com.gamingtracker.application.usecases.PerformCleanup
import com.gamingtracker.application.usecases.ToggleAutostart
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun startKtorServer(
    getGamesUseCase: GetGamesUseCase,
    getSummaryUseCase: GetSummaryUseCase,
    migrateLegacyDataUseCase: MigrateLegacyDataUseCase,
    addGameUseCase: AddGameUseCase,
    getUpdateStatusUseCase: GetUpdateStatusUseCase,
    toggleAutostartUseCase: ToggleAutostart,
    getAutostartStatusUseCase: GetAutostartStatus,
    performCleanupUseCase: PerformCleanup,
    port: Int = 8080,
    wait: Boolean = false
) {
    embeddedServer(Netty, port = port) {
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
        }
        configureRouting(
            getGamesUseCase,
            getSummaryUseCase,
            migrateLegacyDataUseCase,
            addGameUseCase,
            getUpdateStatusUseCase,
            toggleAutostartUseCase,
            getAutostartStatusUseCase,
            performCleanupUseCase
        )
    }.start(wait = wait)
}
