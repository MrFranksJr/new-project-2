package com.gamingtracker.infrastructure.api.ktor.dtos

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
data class AddGameRequest(val name: String, val exeName: String)

@Serializable
data class SummaryDTO(
    val totalPlaytimeMinutes: Long,
    val activeGameName: String?,
    val gamingPCName: String?,
    val systemStats: Map<String, String>
)

@Serializable
data class UpdateStatusDTO(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val downloadUrl: String?
)

@Serializable
data class CleanupRequest(val deleteDb: Boolean)
