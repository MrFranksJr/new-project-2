package com.gaminggaiden.rebirth.application.ports.input

data class GamingSummary(
    val totalPlaytimeMinutes: Long,
    val activeGameName: String?,
    val gamingPCName: String?,
    val systemStats: Map<String, String> = emptyMap()
)

interface GetSummaryUseCase {
    fun execute(): GamingSummary
}
