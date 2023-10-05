package com.kwasowski.sportslife.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.settings.Settings
import com.kwasowski.sportslife.data.settings.SettingsManager
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.domain.settings.GetSettingsUseCase
import com.kwasowski.sportslife.domain.settings.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val mutableState = MutableStateFlow<SettingsViewState>(SettingsViewState.Default)
    val uiState: StateFlow<SettingsViewState> = mutableState.asStateFlow()

    private var currentSettings = Settings()
    var isAfterInitSettings: Boolean = false
    val account = Firebase.auth.currentUser?.email

    init {
        viewModelScope.launch {
            getSettingsUseCase.execute().let {
                when (it) {
                    is Result.Failure -> mutableState.value = SettingsViewState.OnGetSettingsError
                    is Result.Success -> {
                        currentSettings = it.data
                        settingsManager.saveSettings(currentSettings)
                        mutableState.value = SettingsViewState.OnGetSettings(currentSettings)
                    }
                }
            }
        }
    }

    fun onUnitsChanged(units: Units) {
        Timber.d("SettingsViewModel | onUnitsChanged: $units")
        currentSettings.units = units
        updateSettings()

    }

    fun onLanguageChanged(language: String) {
        Timber.d("SettingsViewModel | onLanguageChanged: $language")
        currentSettings.language = language
        updateSettings()
    }


    fun onNotificationAboutTodayTrainingChanged(isChecked: Boolean) {
        Timber.d("SettingsViewModel | onNotificationAboutTodayTrainingChanged: $isChecked")
        currentSettings.notifyTodayTraining = isChecked
        updateSettings()
    }

    fun onNotificationAboutTodaySummaryChanged(isChecked: Boolean) {
        Timber.d("SettingsViewModel | onNotificationAboutDaySummaryChanged: $isChecked")
        currentSettings.notifyDaySummary = isChecked
        updateSettings()
    }

    private fun updateSettings() {
        if (isAfterInitSettings) {
            viewModelScope.launch {
                saveSettingsUseCase.execute(currentSettings)
            }
        } else {
            Timber.d("SettingsViewModel | updateSettings() | isAfterInitSettings: false")
        }
    }
}