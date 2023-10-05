package com.kwasowski.sportslife.domain.category

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.category.CategoryDto
import com.kwasowski.sportslife.data.category.CategoryRepository

class GetCategoriesUseCase(private val categoryRepository: CategoryRepository) {
    suspend fun execute():Result<List<CategoryDto>> {
        Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return when(val result = categoryRepository.getCategories()) {
            is Result.Failure -> result
            is Result.Success -> result
        }
    }
}