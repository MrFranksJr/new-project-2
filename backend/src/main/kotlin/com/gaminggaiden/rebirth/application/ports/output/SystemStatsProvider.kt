package com.gaminggaiden.rebirth.application.ports.output

interface SystemStatsProvider {
    fun getSystemStats(): Map<String, String>
}
