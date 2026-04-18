package com.gamingtracker.infrastructure.api.ktor

import com.gamingtracker.application.ports.input.*
import com.gamingtracker.application.usecases.GetAutostartStatus
import com.gamingtracker.application.usecases.PerformCleanup
import com.gamingtracker.application.usecases.ToggleAutostart
import com.gamingtracker.infrastructure.api.ktor.dtos.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    getGamesUseCase: GetGamesUseCase,
    getSummaryUseCase: GetSummaryUseCase,
    migrateLegacyDataUseCase: MigrateLegacyDataUseCase,
    addGameUseCase: AddGameUseCase,
    getUpdateStatusUseCase: GetUpdateStatusUseCase,
    toggleAutostartUseCase: ToggleAutostart,
    getAutostartStatusUseCase: GetAutostartStatus,
    performCleanupUseCase: PerformCleanup
) {
    routing {
        // Diagnostic endpoint
        get("/api/health") {
            call.respond(mapOf("status" to "ok", "timestamp" to System.currentTimeMillis()))
        }

        // Serve static files from resources
        // We use explicit handling for the root to provide better diagnostics
        get("/") {
            println("[DEBUG] Request for / - serving static/index.html")
            val resource = call.resolveResource("static/index.html")
            if (resource != null) {
                println("[DEBUG] Resource static/index.html found: $resource")
                call.respond(resource)
            } else {
                println("[ERROR] static/index.html not found in resources")
                call.respondText("Error: Frontend assets not found. Please run ./gradlew :backend:processResources", status = HttpStatusCode.InternalServerError)
            }
        }

        // Serve other assets
        staticResources("/", "static")

        route("/api") {
            get("/games") {
                val games = getGamesUseCase.getGames()
                val dtos = games.map {
                    GameDTO(
                        name = it.name,
                        exeName = it.exeName,
                        playtimeMinutes = it.playtimeMinutes,
                        sessionCount = it.sessionCount,
                        lastPlayDate = it.lastPlayDate?.toString(),
                        status = it.status.name
                    )
                }
                call.respond(GamesResponse(dtos))
            }

            post("/games") {
                val request = call.receive<AddGameRequest>()
                val game = addGameUseCase.addGame(request.name, request.exeName)
                call.respond(HttpStatusCode.Created, GameDTO(
                    name = game.name,
                    exeName = game.exeName,
                    playtimeMinutes = game.playtimeMinutes,
                    sessionCount = game.sessionCount,
                    lastPlayDate = game.lastPlayDate?.toString(),
                    status = game.status.name
                ))
            }
            
            get("/summary") {
                val summary = getSummaryUseCase.getSummary()
                val dto = SummaryDTO(
                    totalPlaytimeMinutes = summary.totalPlaytimeMinutes,
                    activeGameName = summary.activeGameName,
                    gamingPCName = summary.gamingPCName,
                    systemStats = summary.systemStats
                )
                call.respond(dto)
            }

            post("/migrate") {
                // Assuming legacy database is in the same directory for simplicity
                migrateLegacyDataUseCase.migrate("GamingGaiden.db")
                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            }

            get("/update-check") {
                val status = getUpdateStatusUseCase.getUpdateStatus()
                call.respond(UpdateStatusDTO(
                    hasUpdate = status.hasUpdate,
                    latestVersion = status.latestVersion,
                    currentVersion = status.currentVersion,
                    downloadUrl = status.downloadUrl
                ))
            }
            get("/autostart") {
                val enabled = getAutostartStatusUseCase()
                call.respond(mapOf("enabled" to enabled))
            }
            post("/autostart") {
                val body = call.receive<Map<String, Boolean>>()
                if ("enabled" !in body) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "enabled is required"))
                } else {
                    val enabled = body["enabled"]!!
                    toggleAutostartUseCase(enabled)
                    call.respond(mapOf("enabled" to enabled))
                }
            }

            post("/cleanup") {
                val request = call.receive<CleanupRequest>()
                val result = performCleanupUseCase(request.deleteDb)
                call.respond(
                    mapOf(
                        "autostartRemoved" to result.autostartRemoved,
                        "dbDeleted" to result.dbDeleted
                    )
                )
            }
        }
    }
}
