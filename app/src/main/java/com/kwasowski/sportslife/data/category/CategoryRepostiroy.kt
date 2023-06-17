package com.kwasowski.sportslife.data.category

import com.kwasowski.sportslife.data.Result

interface CategoryRepository {
    suspend fun getCategories(): Result<List<CategoryDto>>
}