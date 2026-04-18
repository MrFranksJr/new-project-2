package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.GamingPCRepository
import com.gaminggaiden.rebirth.application.ports.output.SessionRepository
import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GamingPC
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import kotlin.test.Test

class TrackGameSessionTest {

    private val gameRepository = mockk<GameRepository>(relaxed = true)
    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val gamingPCRepository = mockk<GamingPCRepository>(relaxed = true)
    private val useCase = TrackGameSession(gameRepository, sessionRepository, gamingPCRepository)

    @Test
    fun `should update game and record session when tracking a game session`() {
        // Given
        val game = Game(name = "Elden Ring", exeName = "eldenring.exe")
        val startTime = Instant.now()
        val duration = 60L

        every { gameRepository.findByExeName("eldenring.exe") } returns game

        // When
        useCase.trackSession("eldenring.exe", startTime, duration)

        // Then
        verify {
            gameRepository.save(match {
                it.name == "Elden Ring" && it.playtimeMinutes == 60L && it.sessionCount == 1
            })
            sessionRepository.save(match {
                it.gameName == "Elden Ring" && it.durationMinutes == 60L && it.startTime == startTime
            })
        }
    }

    @Test
    fun `should update gaming pc playtime and associate it with the game when a game is tracked`() {
        // Given
        val game = Game(name = "Elden Ring", exeName = "eldenring.exe")
        val pc = GamingPC(name = "GamingRig", totalPlaytimeMinutes = 100, inUse = true)
        val startTime = Instant.now()
        val duration = 60L

        every { gameRepository.findByExeName("eldenring.exe") } returns game
        every { gamingPCRepository.findInUse() } returns pc

        // When
        useCase.trackSession("eldenring.exe", startTime, duration)

        // Then
        verify {
            gamingPCRepository.save(match {
                it.name == "GamingRig" && it.totalPlaytimeMinutes == 160L
            })
            gameRepository.save(match {
                it.name == "Elden Ring" && it.gamingPcs.any { pc -> pc.name == "GamingRig" }
            })
        }
    }
}
