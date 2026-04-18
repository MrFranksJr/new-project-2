package com.gamingtracker.application.ports.output

interface VersionProvider {
    fun getLatestVersion(): String
    fun getDownloadUrl(): String?
}
