package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.GetGamesUseCase
import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.domain.Game

class GetGames(private val gameRepository: GameRepository) : GetGamesUseCase {
    override fun getGames(): List<Game> {
        return gameRepository.getAllGames()
    }
}
