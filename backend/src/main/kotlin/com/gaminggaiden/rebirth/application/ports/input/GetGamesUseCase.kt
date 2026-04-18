package com.gaminggaiden.rebirth.application.ports.input

import com.gaminggaiden.rebirth.domain.Game

interface GetGamesUseCase {
    fun getGames(): List<Game>
}
