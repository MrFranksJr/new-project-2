package com.gaminggaiden.rebirth.application.ports.output

interface ProcessMonitor {
    fun getRunningExecutables(): List<String>
    fun isExecutableRunning(exeName: String): Boolean = getRunningExecutables().contains(exeName)
}
