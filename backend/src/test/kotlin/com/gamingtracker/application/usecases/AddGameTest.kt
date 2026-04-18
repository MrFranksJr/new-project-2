package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.domain.Game
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class AddGameTest {
    private val repository = mockk<GameRepository>(relaxed = true)
    private val useCase = AddGame(repository)

    @Test
    fun `should create and save a new game`() {
        val name = "Starfield"
        val exe = "Starfield.exe"
        every { repository.findByName(name) } returns null
        
        val result = useCase.addGame(name, exe)
        
        assertEquals(name, result.name)
        assertEquals(exe, result.exeName)
        verify { repository.save(any()) }
    }

    @Test
    fun `should return existing game if already present`() {
        val name = "Starfield"
        val exe = "Starfield.exe"
        val existingGame = Game(name, exe)
        every { repository.findByName(name) } returns existingGame
        
        val result = useCase.addGame(name, "something_else.exe")
        
        assertEquals(existingGame, result)
        verify(exactly = 0) { repository.save(any()) }
    }
}
