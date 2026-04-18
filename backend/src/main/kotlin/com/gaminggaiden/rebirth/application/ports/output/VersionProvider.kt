package com.gaminggaiden.rebirth.application.ports.output

interface VersionProvider {
    fun getLatestVersion(): String
    fun getDownloadUrl(): String?
}
