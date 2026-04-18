package com.gaminggaiden.rebirth.application.ports.input

interface MigrateLegacyDataUseCase {
    fun migrate(legacyDbPath: String)
}
