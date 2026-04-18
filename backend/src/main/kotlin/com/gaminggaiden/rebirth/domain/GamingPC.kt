package com.gaminggaiden.rebirth.domain

data class GamingPC(
    val name: String,
    val totalPlaytimeMinutes: Long = 0,
    val inUse: Boolean = false
) {
    fun trackPlaytime(durationMinutes: Long): GamingPC {
        return this.copy(
            totalPlaytimeMinutes = this.totalPlaytimeMinutes + durationMinutes
        )
    }
}
