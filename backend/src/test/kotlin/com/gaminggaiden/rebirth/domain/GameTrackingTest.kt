package com.gaminggaiden.rebirth.domain

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
    }
}
