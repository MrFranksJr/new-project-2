package com.gamingtracker.application.ports.input

import com.gamingtracker.domain.Game

interface GetGamesUseCase {
    fun getGames(): List<Game>
}
