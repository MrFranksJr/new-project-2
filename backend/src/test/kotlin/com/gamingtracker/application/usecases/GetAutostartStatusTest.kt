package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.AutostartPort
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetAutostartStatusTest {

    @Test
    fun `returns enabled when port enabled`() {
        val port = mockk<AutostartPort>()
        every { port.isEnabled() } returns true
        val useCase = GetAutostartStatus(port)
        assertTrue(useCase())
    }

    @Test
    fun `returns disabled when port disabled`() {
        val port = mockk<AutostartPort>()
        every { port.isEnabled() } returns false
        val useCase = GetAutostartStatus(port)
        assertFalse(useCase())
    }
}