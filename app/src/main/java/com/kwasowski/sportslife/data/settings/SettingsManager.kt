package com.kwasowski.sportslife.data.settings

import android.content.Context

class SettingsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sports-life-settings", Context.MODE_PRIVATE)

    fun saveSettings(settings: Settings) {
        with(sharedPreferences.edit()) {
            putString("language", settings.language)
            putBoolean("notifyTodayTraining", settings.notifyTodayTraining)
            putBoolean("notifyDaySummary", settings.notifyDaySummary)
            putString("units", settings.units.toString())
            apply()
        }
    }

    fun loadSettings(): Settings {
        return Settings(
            Units.valueOf(sharedPreferences.getString("units", Units.KG_M.toString())!!),
            sharedPreferences.getString("language", "")!!,
            sharedPreferences.getBoolean("notifyTodayTraining", false),
            sharedPreferences.getBoolean("notifyDaySummary", false)
        )
    }
}