package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.AddGameUseCase
import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.domain.Game

class AddGame(private val gameRepository: GameRepository) : AddGameUseCase {
    override fun addGame(name: String, exeName: String): Game {
        val existing = gameRepository.findByName(name)
        if (existing != null) return existing
        
        val newGame = Game(name = name, exeName = exeName)
        gameRepository.save(newGame)
        return newGame
    }
}
