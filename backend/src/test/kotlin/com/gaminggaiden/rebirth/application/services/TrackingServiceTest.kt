package com.gaminggaiden.rebirth.application.services

import com.gaminggaiden.rebirth.application.ports.input.TrackGameSessionUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.ProcessMonitor
import com.gaminggaiden.rebirth.domain.Game
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.*

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
        verify(exactly = 1) { trackGameSessionUseCase.trackSession("Hades.exe", any(), 1L) }
        verify(exactly = 0) { trackGameSessionUseCase.trackSession("eldenring.exe", any(), any()) }
    }

    @Test
    fun `should handle process monitor exceptions gracefully`() {
        // Given
        every { gameRepository.getAllGames() } returns emptyList()
        every { processMonitor.getRunningExecutables() } throws RuntimeException("System error")

        // When & Then
        trackingService.trackOnce() // Should not throw
    }

    @Test
    fun `startTracking starts the timer`() {
        assertFalse(trackingService.isTracking())
        trackingService.startTracking()
        assertTrue(trackingService.isTracking())
    }

    @Test
    fun `startTracking is idempotent`() {
        trackingService.startTracking(1)
        trackingService.startTracking(2)
        assertTrue(trackingService.isTracking())
    }

    @Test
    fun `stopTracking stops the timer`() {
        trackingService.startTracking()
        assertTrue(trackingService.isTracking())
        trackingService.stopTracking()
        assertFalse(trackingService.isTracking())
    }

    @Test
    fun `stopTracking when not tracking does nothing`() {
        assertFalse(trackingService.isTracking())
        trackingService.stopTracking()
        assertFalse(trackingService.isTracking())
    }
}
