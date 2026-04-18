package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GameStatus
import com.gaminggaiden.rebirth.domain.GamingPC
import com.gaminggaiden.rebirth.domain.GamingSession
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class SqliteRepositoryIntegrationTest {

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.drop(GamesTable, GamingSessionsTable, GamingPCsTable, GameGamingPCsTable)
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

        val retrievedByExe = repo.findByExeName("Cyberpunk2077.exe")
        assertNotNull(retrievedByExe)
        assertEquals("Cyberpunk 2077", retrievedByExe.name)
    }

    @Test
    fun `should list all games`() {
        val repo = SqliteGameRepository()
        repo.save(Game("Game 1", "game1.exe"))
        repo.save(Game("Game 2", "game2.exe"))

        val all = repo.getAllGames()
        assertEquals(2, all.size)
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
    fun `should update existing game`() {
        val repo = SqliteGameRepository()
        val game = Game("UpdateMe", "original.exe")
        repo.save(game)

        val updated = game.copy(exeName = "updated.exe", playtimeMinutes = 60)
        repo.save(updated)

        val retrieved = repo.findByName("UpdateMe")
        assertNotNull(retrieved)
        assertEquals("updated.exe", retrieved.exeName)
        assertEquals(60, retrieved.playtimeMinutes)
    }

    @Test
    fun `should track gaming PCs correctly and enforce single in-use`() {
        val pcRepo = SqliteGamingPCRepository()
        val pc1 = GamingPC("SteamDeck", totalPlaytimeMinutes = 500, inUse = true)
        pcRepo.save(pc1)
        
        val pc2 = GamingPC("MainRig", totalPlaytimeMinutes = 1000, inUse = true)
        pcRepo.save(pc2)
        
        val inUse = pcRepo.findInUse()
        assertNotNull(inUse)
        assertEquals("MainRig", inUse.name)
        
        // Verify pc1 is no longer in use
        transaction {
            val pc1InDb = GamingPCsTable.select { GamingPCsTable.name eq "SteamDeck" }.single()
            assertFalse(pc1InDb[GamingPCsTable.inUse])
        }
    }
}
