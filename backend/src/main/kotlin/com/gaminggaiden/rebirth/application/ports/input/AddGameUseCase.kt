package com.gaminggaiden.rebirth.application.ports.input

import com.gaminggaiden.rebirth.domain.Game

interface AddGameUseCase {
    fun execute(name: String, exeName: String): Game
}
