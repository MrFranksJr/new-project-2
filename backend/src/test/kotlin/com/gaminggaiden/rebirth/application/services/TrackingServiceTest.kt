package com.gaminggaiden.rebirth.application.services

import com.gaminggaiden.rebirth.application.ports.input.TrackGameSessionUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.ProcessMonitor
import com.gaminggaiden.rebirth.domain.Game
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class TrackingServiceTest {
    private val gameRepository = mockk<GameRepository>()
    private val processMonitor = mockk<ProcessMonitor>()
    private val trackGameSessionUseCase = mockk<TrackGameSessionUseCase>(relaxed = true)
    private val trackingService = TrackingService(gameRepository, processMonitor, trackGameSessionUseCase)

    @Test
    fun `should track games that are currently running`() {
        // Given
        val games = listOf(
            Game("Hades", "Hades.exe"),
            Game("Elden Ring", "eldenring.exe")
        )
        every { gameRepository.getAllGames() } returns games
        every { processMonitor.getRunningExecutables() } returns listOf("Hades.exe", "chrome.exe")

        // When
        trackingService.trackOnce()

        // Then
        verify(exactly = 1) { trackGameSessionUseCase.execute("Hades.exe", any(), 1L) }
        verify(exactly = 0) { trackGameSessionUseCase.execute("eldenring.exe", any(), any()) }
    }
}
