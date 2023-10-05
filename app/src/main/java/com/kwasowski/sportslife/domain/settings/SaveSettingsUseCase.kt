package com.kwasowski.sportslife.domain.settings

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.settings.Settings
import com.kwasowski.sportslife.data.settings.SettingsRepository

class SaveSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun execute(
        settings: Settings
    ) {
        Firebase.auth.currentUser?.uid?.let {
            settingsRepository.saveSettings(
                uid = it,
                settings = settings
            )
        }
    }
}