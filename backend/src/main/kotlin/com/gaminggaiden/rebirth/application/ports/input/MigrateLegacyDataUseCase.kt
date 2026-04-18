package com.gaminggaiden.rebirth.application.ports.input

interface MigrateLegacyDataUseCase {
    fun execute(legacyDbPath: String)
}
