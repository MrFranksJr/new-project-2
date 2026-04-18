package com.gaminggaiden.rebirth.infrastructure.api.ktor.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    val name: String,
    val exeName: String,
    val playtimeMinutes: Long,
    val sessionCount: Int,
    val lastPlayDate: String?,
    val status: String
)

@Serializable
data class GamesResponse(val games: List<GameDTO>)

@Serializable
data class SummaryDTO(
    val totalPlaytimeMinutes: Long,
    val activeGameName: String?,
    val gamingPCName: String?,
    val systemStats: Map<String, String>
)
