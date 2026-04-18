package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.input.MigrateLegacyDataUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.LegacyDatabasePort
import com.gaminggaiden.rebirth.application.ports.output.SessionRepository

class MigrateLegacyData(
    private val gameRepository: GameRepository,
    private val sessionRepository: SessionRepository,
    private val legacyDatabasePort: LegacyDatabasePort
) : MigrateLegacyDataUseCase {

    override fun execute(legacyDbPath: String) {
        val games = legacyDatabasePort.fetchAllGames()
        games.forEach { gameRepository.save(it) }

        val sessions = legacyDatabasePort.fetchAllSessions()
        sessions.forEach { sessionRepository.save(it) }
    }
}
