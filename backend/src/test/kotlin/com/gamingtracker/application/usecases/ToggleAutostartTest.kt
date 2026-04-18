package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.AutostartPort
import io.mockk.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleAutostartTest {

    private lateinit var port: AutostartPort
    private lateinit var toggleAutostart: ToggleAutostart

    @BeforeTest
    fun setUp() {
        port = mockk(relaxed = true)
        toggleAutostart = ToggleAutostart(port)
    }

    @Test
    fun `when disabled toggle flips to enabled`() {
        every { port.isEnabled() } returns false

        val result = toggleAutostart()

        verify { port.setEnabled(true) }
        assertEquals(true, result)
    }

    @Test
    fun `when enabled toggle flips to disabled`() {
        every { port.isEnabled() } returns true

        val result = toggleAutostart()

        verify { port.setEnabled(false) }
        assertEquals(false, result)
    }

    @Test
    fun `invoke with true sets to enabled`() {
        val result = toggleAutostart(true)

        verify { port.setEnabled(true) }
        assertEquals(true, result)
    }

    @Test
    fun `invoke with false sets to disabled`() {
        val result = toggleAutostart(false)

        verify { port.setEnabled(false) }
        assertEquals(false, result)
    }
}