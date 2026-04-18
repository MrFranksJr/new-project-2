package com.gamingtracker.application.ports.input

interface MigrateLegacyDataUseCase {
    fun migrate(legacyDbPath: String)
}
