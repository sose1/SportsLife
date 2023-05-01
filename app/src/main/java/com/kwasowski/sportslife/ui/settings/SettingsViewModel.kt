package com.kwasowski.sportslife.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.domain.settings.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow<SettingsViewState>(SettingsViewState.Default)
    val uiState: StateFlow<SettingsViewState> = mutableState.asStateFlow()

    private var units = Units.UNKNOWN
    private var language = ""
    private var notifyTodayTraining = false
    private var notifyDaySummary = false
    
    fun onUnitsChanged(units: Units) {
        Timber.d("VIEWMODEL: onUnitsChanged: $units")
        this.units = units
        updateSettings()
    }

    fun onLanguageChanged(language: String) {
        Timber.d("VIEWMODEL: onLanguageChanged: $language")
        this.language = language
        updateSettings()
    }

    fun onNotificationAboutTodayTrainingChanged(isChecked: Boolean) {
        Timber.d("VIEWMODEL: onNotificationAboutTodayTrainingChanged: $isChecked")
        this.notifyTodayTraining = isChecked
        updateSettings()
    }

    fun onNotificationAboutTodaySummaryChanged(isChecked: Boolean) {
        Timber.d("VIEWMODEL: onNotificationAboutDaySummaryChanged: $isChecked")
        this.notifyDaySummary = isChecked
        updateSettings()
    }

    private fun updateSettings() {
        viewModelScope.launch {
            saveSettingsUseCase.execute(
                units = units,
                language = language,
                notifyTodayTraining = notifyTodayTraining,
                notifyDaySummary = notifyDaySummary
            )
        }
    }
}