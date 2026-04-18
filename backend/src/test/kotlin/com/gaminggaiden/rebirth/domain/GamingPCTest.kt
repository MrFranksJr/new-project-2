package com.gaminggaiden.rebirth.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class GamingPCTest {
    @Test
    fun `should increase total playtime`() {
        val pc = GamingPC("MyRig", totalPlaytimeMinutes = 100)
        val updated = pc.trackPlaytime(50)
        assertEquals(150, updated.totalPlaytimeMinutes)
    }
}
