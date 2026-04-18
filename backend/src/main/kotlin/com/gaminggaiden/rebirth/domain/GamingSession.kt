package com.gaminggaiden.rebirth.domain

import java.time.Instant

data class GamingSession(
    val id: Long? = null,
    val gameName: String,
    val startTime: Instant,
    val durationMinutes: Long
)
