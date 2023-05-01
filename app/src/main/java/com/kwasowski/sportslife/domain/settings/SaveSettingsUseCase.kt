package com.kwasowski.sportslife.domain.settings

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.settings.Settings
import com.kwasowski.sportslife.data.settings.SettingsRepository
import com.kwasowski.sportslife.data.settings.Units

class SaveSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun execute(
        units: Units,
        language: String,
        notifyTodayTraining: Boolean,
        notifyDaySummary: Boolean
    ) {
        Firebase.auth.currentUser?.uid?.let {
            settingsRepository.saveSettings(
                uid = it,
                settings = Settings(
                    units = units,
                    language = language,
                    notifyTodayTraining = notifyTodayTraining,
                    notifyDaySummary = notifyDaySummary
                )
            )
        }
    }
}