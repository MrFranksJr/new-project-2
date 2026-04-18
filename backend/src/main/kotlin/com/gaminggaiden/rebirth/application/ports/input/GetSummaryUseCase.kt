package com.gaminggaiden.rebirth.application.ports.input

data class GamingSummary(
    val totalPlaytimeMinutes: Long,
    val activeGameName: String?,
    val gamingPCName: String?
)

interface GetSummaryUseCase {
    fun execute(): GamingSummary
}
