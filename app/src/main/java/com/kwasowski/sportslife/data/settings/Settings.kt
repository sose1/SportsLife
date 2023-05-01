package com.kwasowski.sportslife.data.settings

data class Settings @JvmOverloads constructor(
    val units: Units = Units.UNKNOWN,
    val language: String = "",
    val notifyTodayTraining: Boolean = false,
    val notifyDaySummary: Boolean = false
)

enum class Units {
    UNKNOWN,
    KG_M,
    LBS_MI
}
