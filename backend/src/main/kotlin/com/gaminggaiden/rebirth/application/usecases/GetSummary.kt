package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.input.GamingSummary
import com.gaminggaiden.rebirth.application.ports.input.GetSummaryUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.GamingPCRepository
import com.gaminggaiden.rebirth.application.ports.output.SystemStatsProvider
import com.gaminggaiden.rebirth.domain.GameStatus

class GetSummary(
    private val gameRepository: GameRepository,
    private val pcRepository: GamingPCRepository,
    private val statsProvider: SystemStatsProvider? = null
) : GetSummaryUseCase {
    override fun execute(): GamingSummary {
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
