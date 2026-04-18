package com.gamingtracker.application.usecases

import com.gamingtracker.application.ports.output.VersionProvider
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckForUpdatesTest {
    private val versionProvider = mockk<VersionProvider>()

    @Test
    fun `should return hasUpdate true when versions differ`() {
        val useCase = CheckForUpdates(versionProvider, "1.0.0")
        every { versionProvider.getLatestVersion() } returns "1.0.1"
        every { versionProvider.getDownloadUrl() } returns "http://example.com/download"

        val status = useCase.getUpdateStatus()

        assertTrue(status.hasUpdate)
        assertEquals("1.0.1", status.latestVersion)
        assertEquals("http://example.com/download", status.downloadUrl)
    }

    @Test
    fun `should return hasUpdate false when versions are equal`() {
        val useCase = CheckForUpdates(versionProvider, "1.0.0")
        every { versionProvider.getLatestVersion() } returns "1.0.0"

        val status = useCase.getUpdateStatus()

        assertFalse(status.hasUpdate)
        assertEquals("1.0.0", status.latestVersion)
    }

    @Test
    fun `should return hasUpdate false when latest is older than current`() {
        val useCase = CheckForUpdates(versionProvider, "1.1.0")
        every { versionProvider.getLatestVersion() } returns "1.0.9"

        val status = useCase.getUpdateStatus()

        assertFalse(status.hasUpdate)
    }

    @Test
    fun `should handle different length semver`() {
        val useCase = CheckForUpdates(versionProvider, "1.0")
        every { versionProvider.getLatestVersion() } returns "1.0.1"
        every { versionProvider.getDownloadUrl() } returns null

        val status = useCase.getUpdateStatus()

        assertTrue(status.hasUpdate)
    }

    @Test
    fun `should treat trailing zeros as equal`() {
        val useCase = CheckForUpdates(versionProvider, "1.1")
        every { versionProvider.getLatestVersion() } returns "1.1.0"

        val status = useCase.getUpdateStatus()

        assertFalse(status.hasUpdate)
    }
}
