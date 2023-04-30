package com.kwasowski.sportslife.data.profile


data class Profile @JvmOverloads constructor(
    val gender: Gender = Gender.UNKNOWN,
    val height: Int = 100,
    val weight: Int = 20,
    val bmi: Double = 0.0
)

enum class Gender {
    UNKNOWN,
    WOMAN,
    MAN
}