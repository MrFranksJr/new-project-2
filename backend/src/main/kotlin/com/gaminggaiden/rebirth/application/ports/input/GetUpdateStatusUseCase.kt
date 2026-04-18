package com.gaminggaiden.rebirth.application.ports.input

import com.gaminggaiden.rebirth.domain.UpdateStatus

interface GetUpdateStatusUseCase {
    fun execute(): UpdateStatus
}
