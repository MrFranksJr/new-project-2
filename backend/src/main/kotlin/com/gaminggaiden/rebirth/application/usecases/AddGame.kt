package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.input.AddGameUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.domain.Game

class AddGame(private val gameRepository: GameRepository) : AddGameUseCase {
    override fun execute(name: String, exeName: String): Game {
        val existing = gameRepository.findByName(name)
        if (existing != null) return existing
        
        val newGame = Game(name = name, exeName = exeName)
        gameRepository.save(newGame)
        return newGame
    }
}
