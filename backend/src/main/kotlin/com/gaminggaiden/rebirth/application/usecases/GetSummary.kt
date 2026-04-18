package com.gaminggaiden.rebirth.application.usecases

import com.gaminggaiden.rebirth.application.ports.input.GamingSummary
import com.gaminggaiden.rebirth.application.ports.input.GetSummaryUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.GamingPCRepository

class GetSummary(
    private val gameRepository: GameRepository,
    private val pcRepository: GamingPCRepository
) : GetSummaryUseCase {
    override fun execute(): GamingSummary {
        val games = gameRepository.getAllGames()
        val totalPlaytime = games.sumOf { it.playtimeMinutes }
        val currentPc = pcRepository.findInUse()

        return GamingSummary(
            totalPlaytimeMinutes = totalPlaytime,
            activeGameName = null, // Logic for active game not yet implemented
            gamingPCName = currentPc?.name
        )
    }
}
