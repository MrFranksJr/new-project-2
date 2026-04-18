package com.gamingtracker.infrastructure.system.windows

import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.ports.output.CleanupResult
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.io.File

class WindowsCleanupManager(private val dbPath: String) : CleanupPort {

    private companion object {
        const val REGISTRY_PATH = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
        const val VALUE_NAME = "GamingTracker"
    }

    override fun performCleanup(deleteDb: Boolean): CleanupResult {
        val key = WinReg.HKEY_CURRENT_USER
        val autostartRemoved = if (Advapi32Util.registryValueExists(key, REGISTRY_PATH, VALUE_NAME)) {
            Advapi32Util.registryDeleteValue(key, REGISTRY_PATH, VALUE_NAME)
            true
        } else {
            false
        }

        val dbDeleted = if (deleteDb) {
            val dbFile = File(dbPath)
            dbFile.exists() && dbFile.delete()
        } else {
            false
        }

        return CleanupResult(autostartRemoved = autostartRemoved, dbDeleted = dbDeleted)
    }
}
