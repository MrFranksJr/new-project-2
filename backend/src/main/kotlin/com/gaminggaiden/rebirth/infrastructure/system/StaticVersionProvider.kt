package com.gaminggaiden.rebirth.infrastructure.system

import com.gaminggaiden.rebirth.application.ports.output.VersionProvider

class StaticVersionProvider(
    private val latestVersion: String = "1.0.0",
    private val downloadUrl: String? = "https://github.com/gaminggaiden/rebirth/releases/latest"
) : VersionProvider {
    override fun getLatestVersion(): String = latestVersion
    override fun getDownloadUrl(): String? = downloadUrl
}
