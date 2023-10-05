package com.kwasowski.sportslife.ui.settings

import com.kwasowski.sportslife.data.settings.Settings

sealed class SettingsViewState {
    class OnGetSettings(val settings: Settings) : SettingsViewState()
    object Default : SettingsViewState()
    object OnSelectLanguageClick : SettingsViewState()
    object OnGetSettingsError : SettingsViewState()
}