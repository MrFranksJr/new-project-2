package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.ports.output.CleanupResult

class PerformCleanup(private val cleanupPort: CleanupPort) {
    operator fun invoke(deleteDb: Boolean): CleanupResult = cleanupPort.performCleanup(deleteDb)
}
