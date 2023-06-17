package com.kwasowski.sportslife.data.category

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class CategorySharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sports-life-categories", Context.MODE_PRIVATE)

    fun saveToPreferences(categories: List<CategoryDto>) {
        val categoriesSet = HashSet<String>()
        categories.mapTo(categoriesSet) { "${it.id},${it.PL},${it.EN}" }
        with(sharedPreferences.edit()) {
            putStringSet("categories", categoriesSet)
            apply()
        }
    }

    fun getCategories(): List<CategoryDto> {
        val categoriesList = mutableListOf<CategoryDto>()
        sharedPreferences.getStringSet("categories", emptySet())?.forEach { categoryString ->
            val parts = categoryString.split(",")
            if (parts.size == 3) {
                val id = parts[0]
                val pl = parts[1]
                val en = parts[2]
                val categoryDto = CategoryDto(id, pl, en)
                categoriesList.add(categoryDto)
            }
        }
        return categoriesList
    }

    fun getById(id: String): CategoryDto? {
        return getCategories().find { it.id == id }
    }

    fun getCategoriesByTranslation(translation: String?): CategoryDto? {
        return getCategories().find {
            when(AppCompatDelegate.getApplicationLocales().toLanguageTags()) {
                "en" -> it.EN == translation
                "pl" -> it.PL == translation
                else -> it.EN == translation
            }
        }
    }
}


