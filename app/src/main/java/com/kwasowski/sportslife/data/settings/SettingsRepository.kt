package com.kwasowski.sportslife.data.settings

import com.kwasowski.sportslife.data.Result

interface SettingsRepository {
    suspend fun saveSettings(uid: String, settings: Settings)
    suspend fun getSettings(uid: String): Result<Settings>
}