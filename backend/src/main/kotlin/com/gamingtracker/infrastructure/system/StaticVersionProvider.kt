package com.gamingtracker.infrastructure.system

import com.gamingtracker.application.ports.output.VersionProvider

class StaticVersionProvider(
    private val latestVersion: String = "1.0.0",
    private val downloadUrl: String? = "https://github.com/gamingtracker/releases/latest"
) : VersionProvider {
    override fun getLatestVersion(): String = latestVersion
    override fun getDownloadUrl(): String? = downloadUrl
}
