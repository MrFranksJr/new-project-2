package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.GamingSummary
import com.gamingtracker.application.ports.input.GetSummaryUseCase
import com.gamingtracker.application.ports.output.GameRepository
import com.gamingtracker.application.ports.output.GamingPCRepository
import com.gamingtracker.application.ports.output.SystemStatsProvider
import com.gamingtracker.domain.GameStatus

class GetSummary(
    private val gameRepository: GameRepository,
    private val pcRepository: GamingPCRepository,
    private val statsProvider: SystemStatsProvider? = null
) : GetSummaryUseCase {
    override fun getSummary(): GamingSummary {
        val games = gameRepository.getAllGames()
        val totalPlaytime = games.sumOf { it.playtimeMinutes }
        val currentPc = pcRepository.findInUse()
        val activeGame = games.find { it.status == GameStatus.PLAYING }

        return GamingSummary(
            totalPlaytimeMinutes = totalPlaytime,
            activeGameName = activeGame?.name,
            gamingPCName = currentPc?.name,
            systemStats = statsProvider?.getSystemStats() ?: emptyMap()
        )
    }
}
