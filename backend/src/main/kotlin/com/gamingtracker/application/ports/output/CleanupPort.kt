package com.gamingtracker.application.ports.output

data class CleanupResult(
    val autostartRemoved: Boolean,
    val dbDeleted: Boolean
)

interface CleanupPort {
    fun performCleanup(deleteDb: Boolean): CleanupResult
}
