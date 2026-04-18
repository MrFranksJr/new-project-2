package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.TrackGameSessionUseCase
import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.application.ports.output.GamingPCRepository
import com.gamingtracker.application.ports.output.SessionRepository
import com.gamingtracker.domain.GamingSession
import java.time.Instant

class TrackGameSession(
    private val gameRepository: GameRepository,
    private val sessionRepository: SessionRepository,
    private val gamingPCRepository: GamingPCRepository
) : TrackGameSessionUseCase {

    override fun trackSession(gameExeName: String, startTime: Instant, durationMinutes: Long) {
        val game = gameRepository.findByExeName(gameExeName) ?: return

        val currentPc = gamingPCRepository.findInUse()
        
        var updatedGame = game.trackSession(durationMinutes, startTime)
        
        currentPc?.let { pc ->
            if (updatedGame.gamingPcs.none { it.name == pc.name }) {
                updatedGame = updatedGame.copy(gamingPcs = updatedGame.gamingPcs + pc)
            }
            
            val updatedPc = pc.trackPlaytime(durationMinutes)
            gamingPCRepository.save(updatedPc)
        }
        
        gameRepository.save(updatedGame)

        val session = GamingSession(
            gameName = game.name,
            startTime = startTime,
            durationMinutes = durationMinutes
        )
        sessionRepository.save(session)
    }
}
