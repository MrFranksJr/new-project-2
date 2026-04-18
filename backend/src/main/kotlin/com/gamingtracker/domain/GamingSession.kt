package com.gamingtracker.domain

import java.time.Instant

data class GamingSession(
    val id: Long? = null,
    val gameName: String,
    val startTime: Instant,
    val durationMinutes: Long
)
