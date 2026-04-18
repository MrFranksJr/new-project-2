package com.gamingtracker.infrastructure.api.ktor

import com.gamingtracker.application.ports.input.AddGameUseCase
import com.gamingtracker.application.ports.input.GamingSummary
import com.gamingtracker.application.ports.input.GetGamesUseCase
import com.gamingtracker.application.ports.input.GetSummaryUseCase
import com.gamingtracker.application.ports.input.GetUpdateStatusUseCase
import com.gamingtracker.application.ports.input.MigrateLegacyDataUseCase
import com.gamingtracker.application.ports.output.CleanupResult
import com.gamingtracker.application.usecases.GetAutostartStatus
import com.gamingtracker.application.usecases.PerformCleanup
import com.gamingtracker.application.usecases.ToggleAutostart
import com.gamingtracker.domain.Game
import com.gamingtracker.domain.UpdateStatus
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
    private val toggleAutostartUseCase = mockk<ToggleAutostart>(relaxed = true)
    private val getAutostartStatusUseCase = mockk<GetAutostartStatus>()
    private val performCleanupUseCase = mockk<PerformCleanup>()

    private fun Application.installRouting() {
        install(ContentNegotiation) {
            json()
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
    }

    @Test
    fun `should return games list`() = testApplication {
        application {
            installRouting()
        }
        
        val games = listOf(Game("Hades", "Hades.exe"))
        every { getGamesUseCase.getGames() } returns games
        
        val response = client.get("/api/games")
        
        assertEquals(HttpStatusCode.OK, response.status)
        // Check if response contains "Hades"
        assert(response.bodyAsText().contains("Hades"))
    }

    @Test
    fun `should return summary`() = testApplication {
        application {
            installRouting()
        }
        
        val summary = GamingSummary(totalPlaytimeMinutes = 100, activeGameName = null, gamingPCName = "MyRig")
        every { getSummaryUseCase.getSummary() } returns summary
        
        val response = client.get("/api/summary")
        
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("100"))
        assert(response.bodyAsText().contains("MyRig"))
    }

    @Test
    fun `should add a game`() = testApplication {
        application {
            installRouting()
        }

        val game = Game("Starfield", "Starfield.exe")
        every { addGameUseCase.addGame("Starfield", "Starfield.exe") } returns game

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
            installRouting()
        }

        val updateStatus = UpdateStatus(hasUpdate = true, latestVersion = "1.1.0", currentVersion = "1.0.0", downloadUrl = "http://dl.com")
        every { getUpdateStatusUseCase.getUpdateStatus() } returns updateStatus

        val response = client.get("/api/update-check")

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("1.1.0"))
        assert(response.bodyAsText().contains("true"))
    }

    @Test
    fun `should perform cleanup with delete db flag`() = testApplication {
        application {
            installRouting()
        }

        every { performCleanupUseCase(true) } returns CleanupResult(autostartRemoved = true, dbDeleted = true)

        val response = client.post("/api/cleanup") {
            contentType(ContentType.Application.Json)
            setBody("{\"deleteDb\":true}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("autostartRemoved"))
        assert(response.bodyAsText().contains("dbDeleted"))
    }
}
