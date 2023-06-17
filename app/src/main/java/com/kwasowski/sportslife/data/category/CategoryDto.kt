package com.kwasowski.sportslife.data.category

import androidx.appcompat.app.AppCompatDelegate

data class CategoryDto @JvmOverloads constructor(
    val id: String = "",
    val PL: String = "",
    val EN: String = ""
)

fun CategoryDto.getTranslation(): String = when (
    AppCompatDelegate.getApplicationLocales().toLanguageTags()) {
    "en" -> EN
    "pl" -> PL
    else -> EN
}