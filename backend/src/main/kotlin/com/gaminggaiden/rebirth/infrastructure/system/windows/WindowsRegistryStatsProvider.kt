package com.gaminggaiden.rebirth.infrastructure.system.windows

import com.gaminggaiden.rebirth.application.ports.output.SystemStatsProvider
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg

class WindowsRegistryStatsProvider : SystemStatsProvider {
    private val rootKey = "Software\\HWiNFO64\\VSB"

    override fun getSystemStats(): Map<String, String> {
        val stats = mutableMapOf<String, String>()
        
        try {
            if (!Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, rootKey)) {
                return emptyMap()
            }
            
            val values = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, rootKey)
            
            // HWiNFO64 stores labels as Label0, Label1... and values as Value0, Value1...
            // We'll iterate and pair them up.
            var index = 0
            while (values.containsKey("Label$index") && values.containsKey("Value$index")) {
                val label = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, rootKey, "Label$index")
                val value = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, rootKey, "Value$index")
                stats[label] = value
                index++
            }
        } catch (e: Exception) {
            // Log error or ignore if registry access fails
            return emptyMap()
        }
        
        return stats
    }
}
