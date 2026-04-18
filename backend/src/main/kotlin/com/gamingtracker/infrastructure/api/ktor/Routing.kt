package com.gamingtracker.infrastructure.api.ktor

import com.gamingtracker.application.ports.input.AddGameUseCase
import com.gamingtracker.application.ports.input.GetGamesUseCase
import com.gamingtracker.application.ports.input.GetSummaryUseCase
import com.gamingtracker.application.ports.input.GetUpdateStatusUseCase
import com.gamingtracker.application.usecases.ToggleAutostart
import com.gamingtracker.application.usecases.GetAutostartStatus
import com.gamingtracker.application.usecases.PerformCleanup
import com.gamingtracker.application.ports.input.MigrateLegacyDataUseCase
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
        // Serve static files from resources
        staticResources("/", "static") {
            default("index.html")
        }

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
