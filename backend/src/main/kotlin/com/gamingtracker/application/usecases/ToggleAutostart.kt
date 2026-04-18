package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.AutostartPort

class ToggleAutostart(private val autostartPort: AutostartPort) {
    operator fun invoke(enable: Boolean? = null): Boolean {
        val current = autostartPort.isEnabled()
        val target = enable ?: !current
        autostartPort.setEnabled(target)
        return target
    }
}