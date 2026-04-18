package com.gaminggaiden.rebirth.infrastructure.api.ktor

import com.gaminggaiden.rebirth.application.ports.input.GetGamesUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetSummaryUseCase
import com.gaminggaiden.rebirth.application.ports.input.MigrateLegacyDataUseCase
import com.gaminggaiden.rebirth.infrastructure.api.ktor.dtos.GameDTO
import com.gaminggaiden.rebirth.infrastructure.api.ktor.dtos.GamesResponse
import com.gaminggaiden.rebirth.infrastructure.api.ktor.dtos.SummaryDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    getGamesUseCase: GetGamesUseCase,
    getSummaryUseCase: GetSummaryUseCase,
    migrateLegacyDataUseCase: MigrateLegacyDataUseCase
) {
    routing {
        route("/api") {
            get("/games") {
                val games = getGamesUseCase.execute()
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
            
            get("/summary") {
                val summary = getSummaryUseCase.execute()
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
                migrateLegacyDataUseCase.execute("GamingGaiden.db")
                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            }
        }
    }
}
