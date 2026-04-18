package com.gamingtracker.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class GameTrackingTest {

    @Test
    fun `given a new game when tracked then its playtime increases`() {
        // Given
        val game = Game(name = "Elden Ring", exeName = "eldenring.exe")
        val sessionDuration = 120L // 2 hours

        // When
        val updatedGame = game.trackSession(sessionDuration, Instant.now())

        // Then
        assertEquals(120L, updatedGame.playtimeMinutes)
        assertEquals(1, updatedGame.sessionCount)
        assertEquals(GameStatus.PLAYING, updatedGame.status)
    }

    @Test
    fun `given a finished game when tracked then its status remains finished`() {
        // Given
        val game = Game(name = "Hades", exeName = "Hades.exe", status = GameStatus.FINISHED)
        
        // When
        val updatedGame = game.trackSession(30, Instant.now())
        
        // Then
        assertEquals(GameStatus.FINISHED, updatedGame.status)
        assertEquals(30, updatedGame.playtimeMinutes)
    }
}
