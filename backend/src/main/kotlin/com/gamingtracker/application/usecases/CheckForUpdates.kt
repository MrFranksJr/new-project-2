package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.input.GetUpdateStatusUseCase
import com.gamingtracker.application.ports.output.VersionProvider
import com.gamingtracker.domain.UpdateStatus

class CheckForUpdates(
    private val versionProvider: VersionProvider,
    private val currentVersion: String
) : GetUpdateStatusUseCase {
    
    override fun getUpdateStatus(): UpdateStatus {
        val latest = versionProvider.getLatestVersion()
        val hasUpdate = isNewer(latest, currentVersion)
        
        return UpdateStatus(
            hasUpdate = hasUpdate,
            latestVersion = latest,
            currentVersion = currentVersion,
            downloadUrl = if (hasUpdate) versionProvider.getDownloadUrl() else null
        )
    }
    
    private fun isNewer(latest: String, current: String): Boolean {
        if (latest == current) return false
        
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        
        val maxParts = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxParts) {
            val latestPart = latestParts.getOrElse(i) { 0 }
            val currentPart = currentParts.getOrElse(i) { 0 }
            
            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }
        
        return false
    }
}
