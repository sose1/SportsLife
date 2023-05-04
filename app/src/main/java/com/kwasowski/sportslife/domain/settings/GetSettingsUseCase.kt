package com.kwasowski.sportslife.domain.settings

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.settings.Settings
import com.kwasowski.sportslife.data.settings.SettingsRepository

class GetSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun execute(): Result<Settings> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return when (val result = settingsRepository.getSettings(uid)) {
            is Result.Failure -> if (result.exception is NullPointerException) Result.Success(
                Settings()
            ) else result

            is Result.Success -> result
        }
    }
}