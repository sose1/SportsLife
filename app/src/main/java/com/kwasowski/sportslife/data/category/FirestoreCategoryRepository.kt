package com.kwasowski.sportslife.data.category

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirestoreCategoryRepository : CategoryRepository {
    private val path = "categories"
    private val collection = Firebase.firestore.collection(path)

    override suspend fun getCategories(): Result<List<CategoryDto>> =
        suspendCoroutine { continuation ->
            collection.get()
                .addOnSuccessListener { querySnapshot ->
                    val categoryList = mutableListOf<CategoryDto>()
                    querySnapshot.documents.forEach {
                        val category = it.toObject<Category>()
                        if (category != null) {
                            categoryList.add(
                                CategoryDto(
                                    it.id,
                                    category.PL,
                                    category.EN
                                )
                            )
                        }
                    }
                    continuation.resume(Result.Success(categoryList))
                }.addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }
}