package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.AutostartPort

class GetAutostartStatus(private val autostartPort: AutostartPort) {
    operator fun invoke(): Boolean = autostartPort.isEnabled()
}