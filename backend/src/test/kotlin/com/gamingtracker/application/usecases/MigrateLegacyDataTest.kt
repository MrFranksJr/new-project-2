package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.application.ports.output.LegacyDatabasePort
import com.gamingtracker.application.ports.output.SessionRepository
import com.gamingtracker.domain.Game
import com.gamingtracker.domain.GamingSession
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import kotlin.test.Test

class MigrateLegacyDataTest {

    private val gameRepository = mockk<GameRepository>(relaxed = true)
    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val legacyDatabasePort = mockk<LegacyDatabasePort>()
    private val migrateLegacyData = MigrateLegacyData(gameRepository, sessionRepository, legacyDatabasePort)

    @Test
    fun `should migrate games and sessions from legacy database`() {
        // Given
        val legacyGames = listOf(
            Game(name = "The Witcher 3", exeName = "witcher3.exe", playtimeMinutes = 500),
            Game(name = "Cyberpunk 2077", exeName = "cyberpunk.exe", playtimeMinutes = 1000)
        )
        val legacySessions = listOf(
            GamingSession(gameName = "The Witcher 3", startTime = Instant.now(), durationMinutes = 60),
            GamingSession(gameName = "Cyberpunk 2077", startTime = Instant.now(), durationMinutes = 120)
        )

        every { legacyDatabasePort.fetchAllGames() } returns legacyGames
        every { legacyDatabasePort.fetchAllSessions() } returns legacySessions

        // When
        migrateLegacyData.migrate("path/to/legacy.db")

        // Then
        verify(exactly = 2) { gameRepository.save(any()) }
        verify(exactly = 2) { sessionRepository.save(any()) }
        verify { gameRepository.save(match { it.name == "The Witcher 3" }) }
        verify { gameRepository.save(match { it.name == "Cyberpunk 2077" }) }
    }
}
