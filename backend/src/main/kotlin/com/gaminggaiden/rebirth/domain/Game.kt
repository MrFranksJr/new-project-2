package com.gaminggaiden.rebirth.domain

import com.gaminggaiden.rebirth.domain.GameStatus.PLAYING
import com.gaminggaiden.rebirth.domain.GameStatus.UNPLAYED
import java.time.Instant

data class Game(
    val name: String,
    val exeName: String,
    val playtimeMinutes: Long = 0,
    val sessionCount: Int = 0,
    val lastPlayDate: Instant? = null,
    val status: GameStatus = UNPLAYED,
    val gamingPcs: List<GamingPC> = emptyList()
) {
    fun trackSession(durationMinutes: Long, startTime: Instant): Game {
        return this.copy(
            playtimeMinutes = this.playtimeMinutes + durationMinutes,
            sessionCount = this.sessionCount + 1,
            lastPlayDate = startTime,
            status = if (this.status == UNPLAYED) PLAYING else this.status
        )
    }
}
