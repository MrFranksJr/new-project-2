package com.gaminggaiden.rebirth.application.ports.input

import java.time.Instant

interface TrackGameSessionUseCase {
    fun execute(gameExeName: String, startTime: Instant, durationMinutes: Long)
}
