package com.kwasowski.sportslife.ui.settings

sealed class SettingsViewState {
    object Default : SettingsViewState()
    object OnSelectLanguageClick : SettingsViewState()
    object OnGetSettingsError : SettingsViewState()
}