package com.gamingtracker.infrastructure.system.windows

import com.gamingtracker.application.ports.output.AutostartPort
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.lang.ProcessHandle

class WindowsAutostartManager : AutostartPort {

    private companion object {
        const val REGISTRY_PATH = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
        const val VALUE_NAME = "GamingTracker"
    }

    override fun isEnabled(): Boolean = Advapi32Util.registryValueExists(
        WinReg.HKEY_CURRENT_USER,
        REGISTRY_PATH,
        VALUE_NAME
    )

    override fun setEnabled(enabled: Boolean) {
        val exePath = ProcessHandle.current().info().command().orElseThrow {
            IllegalStateException("Cannot determine executable path")
        }

        val key = WinReg.HKEY_CURRENT_USER
        val subKey = REGISTRY_PATH
        val valueName = VALUE_NAME

        if (enabled) {
            Advapi32Util.registrySetStringValue(key, subKey, valueName, exePath)
        } else {
            if (Advapi32Util.registryValueExists(key, subKey, valueName)) {
                Advapi32Util.registryDeleteValue(key, subKey, valueName)
            }
        }
    }
}