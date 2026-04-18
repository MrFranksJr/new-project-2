package com.gamingtracker

import com.gamingtracker.application.ports.output.ProcessMonitor
import com.gamingtracker.application.ports.output.SystemStatsProvider
import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.ports.output.CleanupResult
import com.gamingtracker.application.services.TrackingService
import com.gamingtracker.application.usecases.*
import com.gamingtracker.infrastructure.api.ktor.configureRouting
import com.gamingtracker.infrastructure.api.ktor.dtos.AddGameRequest
import com.gamingtracker.infrastructure.api.ktor.dtos.GamesResponse
import com.gamingtracker.infrastructure.api.ktor.dtos.SummaryDTO
import com.gamingtracker.infrastructure.persistence.sqlite.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class E2EJourneyTest {

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:e2e;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(GamesTable, GamingSessionsTable, GamingPCsTable, GameGamingPCsTable)
            // Setup a default PC
            GamingPCsTable.insert {
                it[name] = "TestRig"
                it[inUse] = true
                it[totalPlaytimeMinutes] = 0
            }
        }
    }

    @Test
    fun `full user journey - add game, track session, and verify summary`() = testApplication {
        // Setup infrastructure mocks
        val processMonitor = mockk<ProcessMonitor>()
        val statsProvider = mockk<SystemStatsProvider>()
        
        every { statsProvider.getSystemStats() } returns mapOf("CPU" to "50%", "GPU" to "60%")
        
        // Initialize real repositories with the in-memory DB
        val gameRepo = SqliteGameRepository()
        val pcRepo = SqliteGamingPCRepository()
        val sessionRepo = SqliteSessionRepository()
        val legacyAdapter = mockk<SqliteLegacyDatabaseAdapter>() // Not used in this journey

        // Use cases
        val addGame = AddGame(gameRepo)
        val getGames = GetGames(gameRepo)
        val getSummary = GetSummary(gameRepo, pcRepo, statsProvider)
        val trackSession = TrackGameSession(gameRepo, sessionRepo, pcRepo)
        val migrate = MigrateLegacyData(gameRepo, sessionRepo, legacyAdapter)
        val checkForUpdates = CheckForUpdates(mockk(relaxed = true), "1.0.0")
        val toggleAutostart = ToggleAutostart(mockk(relaxed = true))
        val getAutostartStatus = GetAutostartStatus(mockk(relaxed = true))
        val performCleanup = PerformCleanup(object : CleanupPort {
            override fun performCleanup(deleteDb: Boolean): CleanupResult = CleanupResult(false, false)
        })

        // Tracking service
        val trackingService = TrackingService(gameRepo, processMonitor, trackSession)

        application {
            install(ServerContentNegotiation) {
                json()
            }
            configureRouting(getGames, getSummary, migrate, addGame, checkForUpdates, toggleAutostart, getAutostartStatus, performCleanup)
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        // 1. Add a new game via API
        val addResponse = client.post("/api/games") {
            contentType(ContentType.Application.Json)
            setBody(AddGameRequest("Hades II", "Hades2.exe"))
        }
        assertEquals(HttpStatusCode.Created, addResponse.status)

        // 2. Verify game is in the list
        val gamesResponse = client.get("/api/games").body<GamesResponse>()
        assertTrue(gamesResponse.games.any { it.name == "Hades II" && it.playtimeMinutes == 0L })

        // 3. Simulate game running
        every { processMonitor.getRunningExecutables() } returns listOf("Hades2.exe", "explorer.exe")
        
        // 4. Trigger tracking (simulating 1 minute of play)
        trackingService.trackOnce()

        // 5. Verify summary via API
        val summaryResponse = client.get("/api/summary").body<SummaryDTO>()
        assertEquals(1L, summaryResponse.totalPlaytimeMinutes)
        assertEquals("Hades II", summaryResponse.activeGameName)
        assertEquals("TestRig", summaryResponse.gamingPCName)
        assertEquals("50%", summaryResponse.systemStats["CPU"])

        // 6. Verify game stats updated
        val updatedGamesResponse = client.get("/api/games").body<GamesResponse>()
        val hades = updatedGamesResponse.games.find { it.name == "Hades II" }!!
        assertEquals(1L, hades.playtimeMinutes)
        assertEquals(1, hades.sessionCount)
    }
}
