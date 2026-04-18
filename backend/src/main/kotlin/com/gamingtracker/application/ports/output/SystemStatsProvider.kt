package com.gamingtracker.application.ports.output

interface SystemStatsProvider {
    fun getSystemStats(): Map<String, String>
}
