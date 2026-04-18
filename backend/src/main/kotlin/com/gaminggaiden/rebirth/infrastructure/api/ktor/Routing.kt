package com.gaminggaiden.rebirth.infrastructure.api.ktor

import com.gaminggaiden.rebirth.application.ports.input.AddGameUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetGamesUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetSummaryUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetUpdateStatusUseCase
import com.gaminggaiden.rebirth.application.ports.input.MigrateLegacyDataUseCase
import com.gaminggaiden.rebirth.infrastructure.api.ktor.dtos.*
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
    getUpdateStatusUseCase: GetUpdateStatusUseCase
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
        }
    }
}
