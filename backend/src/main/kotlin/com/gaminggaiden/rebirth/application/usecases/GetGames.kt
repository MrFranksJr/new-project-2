package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.input.GetGamesUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.domain.Game

class GetGames(private val gameRepository: GameRepository) : GetGamesUseCase {
    override fun getGames(): List<Game> {
        return gameRepository.getAllGames()
    }
}
