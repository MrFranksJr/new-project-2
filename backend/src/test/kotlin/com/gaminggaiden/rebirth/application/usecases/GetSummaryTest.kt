package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.GamingPCRepository
import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GamingPC
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSummaryTest {
    private val gameRepository = mockk<GameRepository>()
    private val pcRepository = mockk<GamingPCRepository>()
    private val useCase = GetSummary(gameRepository, pcRepository)

    @Test
    fun `should calculate summary correctly`() {
        val games = listOf(
            Game("Hades", "Hades.exe", playtimeMinutes = 100),
            Game("Celeste", "Celeste.exe", playtimeMinutes = 50)
        )
        val pc = GamingPC("MyRig", inUse = true)

        every { gameRepository.getAllGames() } returns games
        every { pcRepository.findInUse() } returns pc

        val summary = useCase.execute()

        assertEquals(150, summary.totalPlaytimeMinutes)
        assertEquals("MyRig", summary.gamingPCName)
    }
}
