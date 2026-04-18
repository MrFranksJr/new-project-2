package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GameStatus
import com.gaminggaiden.rebirth.domain.GamingPC
import com.gaminggaiden.rebirth.domain.GamingSession
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SqliteRepositoryIntegrationTest {

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(GamesTable, GamingSessionsTable, GamingPCsTable, GameGamingPCsTable)
        }
    }

    @Test
    fun `should save and retrieve a game with its PCs`() {
        val repo = SqliteGameRepository()
        val pc = GamingPC("MyRig", totalPlaytimeMinutes = 10, inUse = true)
        val game = Game(
            name = "Cyberpunk 2077",
            exeName = "Cyberpunk2077.exe",
            playtimeMinutes = 120,
            sessionCount = 2,
            lastPlayDate = Instant.now().truncatedTo(ChronoUnit.SECONDS),
            status = GameStatus.PLAYING,
            gamingPcs = listOf(pc)
        )

        repo.save(game)
        
        val retrieved = repo.findByName("Cyberpunk 2077")
        assertNotNull(retrieved)
        assertEquals(game.name, retrieved.name)
        assertEquals(game.playtimeMinutes, retrieved.playtimeMinutes)
        assertEquals(game.status, retrieved.status)
        assertEquals(1, retrieved.gamingPcs.size)
        assertEquals("MyRig", retrieved.gamingPcs[0].name)
    }

    @Test
    fun `should save gaming session`() {
        val gameRepo = SqliteGameRepository()
        val sessionRepo = SqliteSessionRepository()
        
        val game = Game("Hades", "Hades.exe")
        gameRepo.save(game)
        
        val startTime = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        val session = GamingSession(gameName = "Hades", startTime = startTime, durationMinutes = 45)
        
        sessionRepo.save(session)
        
        // Verified by checking if no exception is thrown, 
        // in a real scenario we'd add a findSessionsForGame method to the port/adapter
    }

    @Test
    fun `should track gaming PCs correctly`() {
        val pcRepo = SqliteGamingPCRepository()
        val pc = GamingPC("SteamDeck", totalPlaytimeMinutes = 500, inUse = false)
        
        pcRepo.save(pc)
        pcRepo.save(pc.copy(inUse = true, totalPlaytimeMinutes = 510))
        
        val inUse = pcRepo.findInUse()
        assertNotNull(inUse)
        assertEquals("SteamDeck", inUse.name)
        assertEquals(510, inUse.totalPlaytimeMinutes)
    }
}
