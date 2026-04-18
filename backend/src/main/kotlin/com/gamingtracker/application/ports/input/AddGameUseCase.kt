package com.gamingtracker.application.ports.input

import com.gamingtracker.domain.Game

interface AddGameUseCase {
    fun addGame(name: String, exeName: String): Game
}
