package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.domain.GameStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqliteLegacyDatabaseAdapterTest {

    private val legacyDbPath = "legacy_test.db"

    @BeforeTest
    fun setup() {
        val db = Database.connect("jdbc:h2:mem:legacy;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.drop(SqliteLegacyDatabaseAdapter.LegacyGamesTable, SqliteLegacyDatabaseAdapter.LegacySessionsTable)
            SchemaUtils.create(SqliteLegacyDatabaseAdapter.LegacyGamesTable, SqliteLegacyDatabaseAdapter.LegacySessionsTable)
            
            SqliteLegacyDatabaseAdapter.LegacyGamesTable.insert {
                it[name] = "Skyrim"
                it[exeName] = "TESV.exe"
                it[playtimeMinutes] = 3000
                it[sessionCount] = 50
                it[lastPlayDate] = Instant.now().truncatedTo(ChronoUnit.SECONDS)
                it[status] = GameStatus.FINISHED.name
            }
            
            SqliteLegacyDatabaseAdapter.LegacySessionsTable.insert {
                it[gameName] = "Skyrim"
                it[startTime] = Instant.now().truncatedTo(ChronoUnit.SECONDS)
                it[durationMinutes] = 120
            }
        }
    }

    @Test
    fun `should fetch all games from legacy database`() {
        // Given
        val db = Database.connect("jdbc:h2:mem:legacy;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        val adapter = SqliteLegacyDatabaseAdapter(db)

        // When
        val games = adapter.fetchAllGames()

        // Then
        assertEquals(1, games.size)
        assertEquals("Skyrim", games[0].name)
        assertEquals(3000, games[0].playtimeMinutes)
        assertEquals(GameStatus.FINISHED, games[0].status)
    }

    @Test
    fun `should fetch all sessions from legacy database`() {
        // Given
        val db = Database.connect("jdbc:h2:mem:legacy;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        val adapter = SqliteLegacyDatabaseAdapter(db)

        // When
        val sessions = adapter.fetchAllSessions()

        // Then
        assertEquals(1, sessions.size)
        assertEquals("Skyrim", sessions[0].gameName)
        assertEquals(120, sessions[0].durationMinutes)
    }

    @Test
    fun `should handle invalid game status in legacy database`() {
        // Given
        val db = Database.connect("jdbc:h2:mem:legacy;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction(db) {
            SqliteLegacyDatabaseAdapter.LegacyGamesTable.insert {
                it[name] = "BrokenGame"
                it[exeName] = "broken.exe"
                it[status] = "INVALID_STATUS"
            }
        }
        val adapter = SqliteLegacyDatabaseAdapter(db)

        // When
        val games = adapter.fetchAllGames()

        // Then
        val brokenGame = games.find { it.name == "BrokenGame" }
        assertEquals(GameStatus.UNPLAYED, brokenGame?.status)
    }
}
