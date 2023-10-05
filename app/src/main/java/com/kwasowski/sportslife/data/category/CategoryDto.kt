package com.kwasowski.sportslife.data.category

import androidx.appcompat.app.AppCompatDelegate
import com.kwasowski.sportslife.utils.LanguageTag

data class CategoryDto @JvmOverloads constructor(
    val id: String = "",
    val PL: String = "",
    val EN: String = ""
)

fun CategoryDto.getTranslation(): String = when (
    AppCompatDelegate.getApplicationLocales().toLanguageTags()) {
    LanguageTag.EN -> EN
    LanguageTag.PL -> PL
    else -> EN
}