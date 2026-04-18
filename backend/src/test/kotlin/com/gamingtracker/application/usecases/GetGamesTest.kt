package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.domain.Game
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class GetGamesTest {
    private val gameRepository = mockk<GameRepository>()
    private val useCase = GetGames(gameRepository)

    @Test
    fun `should return all games from repository`() {
        val games = listOf(Game("Hades", "Hades.exe"), Game("Celeste", "Celeste.exe"))
        every { gameRepository.getAllGames() } returns games

        val result = useCase.getGames()

        assertEquals(games, result)
    }
}
