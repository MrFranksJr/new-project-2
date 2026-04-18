package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.MigrateLegacyDataUseCase
import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.application.ports.output.LegacyDatabasePort
import com.gamingtracker.application.ports.output.SessionRepository

class MigrateLegacyData(
    private val gameRepository: GameRepository,
    private val sessionRepository: SessionRepository,
    private val legacyDatabasePort: LegacyDatabasePort
) : MigrateLegacyDataUseCase {

    override fun migrate(legacyDbPath: String) {
        val games = legacyDatabasePort.fetchAllGames()
        games.forEach { gameRepository.save(it) }

        val sessions = legacyDatabasePort.fetchAllSessions()
        sessions.forEach { sessionRepository.save(it) }
    }
}
