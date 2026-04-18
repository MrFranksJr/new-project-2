package com.gamingtracker.application.ports.output

interface AutostartPort {
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}