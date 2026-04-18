package com.gamingtracker.application.ports.input

import com.gamingtracker.domain.UpdateStatus

interface GetUpdateStatusUseCase {
    fun getUpdateStatus(): UpdateStatus
}
