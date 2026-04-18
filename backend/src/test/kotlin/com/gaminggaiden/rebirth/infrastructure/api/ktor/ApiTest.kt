package com.gaminggaiden.rebirth.infrastructure.api.ktor

import com.gaminggaiden.rebirth.application.ports.input.AddGameUseCase
import com.gaminggaiden.rebirth.application.ports.input.GamingSummary
import com.gaminggaiden.rebirth.application.ports.input.GetGamesUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetSummaryUseCase
import com.gaminggaiden.rebirth.application.ports.input.GetUpdateStatusUseCase
import com.gaminggaiden.rebirth.application.ports.input.MigrateLegacyDataUseCase
import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.UpdateStatus
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiTest {
    private val getGamesUseCase = mockk<GetGamesUseCase>()
    private val getSummaryUseCase = mockk<GetSummaryUseCase>()
    private val migrateLegacyDataUseCase = mockk<MigrateLegacyDataUseCase>()
    private val addGameUseCase = mockk<AddGameUseCase>()
    private val getUpdateStatusUseCase = mockk<GetUpdateStatusUseCase>()

    @Test
    fun `should return games list`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(getGamesUseCase, getSummaryUseCase, migrateLegacyDataUseCase, addGameUseCase, getUpdateStatusUseCase)
        }
        
        val games = listOf(Game("Hades", "Hades.exe"))
        every { getGamesUseCase.execute() } returns games
        
        val response = client.get("/api/games")
        
        assertEquals(HttpStatusCode.OK, response.status)
        // Check if response contains "Hades"
        assert(response.bodyAsText().contains("Hades"))
    }

    @Test
    fun `should return summary`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(getGamesUseCase, getSummaryUseCase, migrateLegacyDataUseCase, addGameUseCase, getUpdateStatusUseCase)
        }
        
        val summary = GamingSummary(totalPlaytimeMinutes = 100, activeGameName = null, gamingPCName = "MyRig")
        every { getSummaryUseCase.execute() } returns summary
        
        val response = client.get("/api/summary")
        
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("100"))
        assert(response.bodyAsText().contains("MyRig"))
    }

    @Test
    fun `should add a game`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(getGamesUseCase, getSummaryUseCase, migrateLegacyDataUseCase, addGameUseCase, getUpdateStatusUseCase)
        }

        val game = Game("Starfield", "Starfield.exe")
        every { addGameUseCase.execute("Starfield", "Starfield.exe") } returns game

        val response = client.post("/api/games") {
            contentType(ContentType.Application.Json)
            setBody("{\"name\":\"Starfield\", \"exeName\":\"Starfield.exe\"}")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assert(response.bodyAsText().contains("Starfield"))
    }

    @Test
    fun `should check for updates`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(getGamesUseCase, getSummaryUseCase, migrateLegacyDataUseCase, addGameUseCase, getUpdateStatusUseCase)
        }

        val updateStatus = UpdateStatus(hasUpdate = true, latestVersion = "1.1.0", currentVersion = "1.0.0", downloadUrl = "http://dl.com")
        every { getUpdateStatusUseCase.execute() } returns updateStatus

        val response = client.get("/api/update-check")

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("1.1.0"))
        assert(response.bodyAsText().contains("true"))
    }
}
