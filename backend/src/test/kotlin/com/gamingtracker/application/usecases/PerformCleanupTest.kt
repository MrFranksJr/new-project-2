package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.CleanupPort
import com.gamingtracker.application.ports.output.CleanupResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PerformCleanupTest {

    private lateinit var cleanupPort: CleanupPort
    private lateinit var performCleanup: PerformCleanup

    @BeforeTest
    fun setUp() {
        cleanupPort = mockk()
        performCleanup = PerformCleanup(cleanupPort)
    }

    @Test
    fun `forwards deleteDb true and returns cleanup result`() {
        val expected = CleanupResult(autostartRemoved = true, dbDeleted = true)
        every { cleanupPort.performCleanup(true) } returns expected

        val result = performCleanup(true)

        verify { cleanupPort.performCleanup(true) }
        assertEquals(expected, result)
    }

    @Test
    fun `forwards deleteDb false and returns cleanup result`() {
        val expected = CleanupResult(autostartRemoved = true, dbDeleted = false)
        every { cleanupPort.performCleanup(false) } returns expected

        val result = performCleanup(false)

        verify { cleanupPort.performCleanup(false) }
        assertEquals(expected, result)
    }
}
