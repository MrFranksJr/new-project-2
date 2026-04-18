package com.gaminggaiden.rebirth.application.services

import com.gaminggaiden.rebirth.application.ports.input.TrackGameSessionUseCase
import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.application.ports.output.ProcessMonitor
import java.time.Instant
import java.util.*
import kotlin.concurrent.timer

class TrackingService(
    private val gameRepository: GameRepository,
    private val processMonitor: ProcessMonitor,
    private val trackGameSessionUseCase: TrackGameSessionUseCase
) {
    private var timer: Timer? = null

    fun startTracking(periodMinutes: Long = 1) {
        if (timer != null) return
        
        timer = timer(name = "TrackingService", period = periodMinutes * 60 * 1000) {
            trackOnce()
        }
    }

    fun stopTracking() {
        timer?.cancel()
        timer = null
    }

    fun trackOnce() {
        val runningExecutables = try {
            processMonitor.getRunningExecutables()
        } catch (e: Exception) {
            emptyList()
        }
        
        val allGames = gameRepository.getAllGames()
        
        allGames.forEach { game ->
            if (runningExecutables.contains(game.exeName)) {
                trackGameSessionUseCase.execute(game.exeName, Instant.now(), 1L)
            }
        }
    }
}
