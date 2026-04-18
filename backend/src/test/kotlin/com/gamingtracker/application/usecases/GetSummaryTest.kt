package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.application.ports.output.GamingPCRepository
import com.gamingtracker.application.ports.output.SystemStatsProvider
import com.gamingtracker.domain.Game
import com.gamingtracker.domain.GamingPC
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSummaryTest {
    private val gameRepository = mockk<GameRepository>()
    private val pcRepository = mockk<GamingPCRepository>()
    private val statsProvider = mockk<SystemStatsProvider>()
    private val useCase = GetSummary(gameRepository, pcRepository, statsProvider)

    @Test
    fun `should calculate summary correctly`() {
        val games = listOf(
            Game("Hades", "Hades.exe", playtimeMinutes = 100),
            Game("Celeste", "Celeste.exe", playtimeMinutes = 50)
        )
        val pc = GamingPC("MyRig", inUse = true)
        val stats = mapOf("CPU" to "50%", "GPU" to "60%")

        every { gameRepository.getAllGames() } returns games
        every { pcRepository.findInUse() } returns pc
        every { statsProvider.getSystemStats() } returns stats

        val summary = useCase.getSummary()

        assertEquals(150, summary.totalPlaytimeMinutes)
        assertEquals("MyRig", summary.gamingPCName)
        assertEquals(stats, summary.systemStats)
    }

    @Test
    fun `should handle missing stats provider`() {
        val simpleUseCase = GetSummary(gameRepository, pcRepository, null)
        every { gameRepository.getAllGames() } returns emptyList()
        every { pcRepository.findInUse() } returns null

        val summary = simpleUseCase.getSummary()

        assertEquals(0, summary.totalPlaytimeMinutes)
        assertTrue(summary.systemStats.isEmpty())
    }
}
