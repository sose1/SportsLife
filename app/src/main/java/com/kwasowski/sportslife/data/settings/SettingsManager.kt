package com.kwasowski.sportslife.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.kwasowski.sportslife.utils.Constants
import com.kwasowski.sportslife.utils.LanguageTag

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    fun saveSettings(settings: Settings) {
        with(sharedPreferences.edit()) {
            putString(Constants.SETTINGS_LANGUAGE_KEY, settings.language)
            putBoolean(Constants.SETTINGS_NOTIFY_TODAY_TRAINING_KEY, settings.notifyTodayTraining)
            putBoolean(Constants.SETTINGS_NOTIFY_DAY_SUMMARY_KEY, settings.notifyDaySummary)
            putString(Constants.SETTINGS_UNITS_KEY, settings.units.toString())
            apply()
        }
    }

    fun loadSettings(): Settings {
        return Settings(
            units = Units.valueOf(sharedPreferences.getString(Constants.SETTINGS_UNITS_KEY, Units.KG_M.toString())!!),
            language = sharedPreferences.getString(Constants.SETTINGS_LANGUAGE_KEY, LanguageTag.EN)!!,
            notifyTodayTraining = sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFY_TODAY_TRAINING_KEY, false),
            notifyDaySummary = sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFY_DAY_SUMMARY_KEY, false)
        )
    }
}