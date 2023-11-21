package com.kwasowski.sportslife.data.settings

data class Settings @JvmOverloads constructor(
    var units: Units = Units.KG_M,
    var language: String = "",
)

enum class Units {
    KG_M,
    LBS_MI
}
