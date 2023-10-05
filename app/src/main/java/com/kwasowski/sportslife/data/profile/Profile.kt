package com.kwasowski.sportslife.data.profile


data class Profile @JvmOverloads constructor(
    val gender: Gender = Gender.UNKNOWN,
    val height: Int = 170,
    val weight: Int = 70,
    val bmi: Double = 0.0
)

enum class Gender {
    UNKNOWN,
    WOMAN,
    MAN
}