package com.gamingtracker.domain

data class UpdateStatus(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val downloadUrl: String? = null
)
