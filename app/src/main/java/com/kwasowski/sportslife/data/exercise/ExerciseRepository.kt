package com.kwasowski.sportslife.data.exercise

import com.kwasowski.sportslife.data.Result

interface ExerciseRepository {
    suspend fun saveExercise(id: String?, exercise: Exercise): Result<Unit>
    suspend fun getExerciseListByOwnerId(ownerId: String): Result<List<ExerciseDto>>
    suspend fun deleteExercise(id: String): Result<Unit>
    suspend fun getSharedExercises(ownerId: String): Result<List<ExerciseDto>>
    suspend fun getExerciseById(exerciseId: String): Result<ExerciseDto>
    suspend fun getFavoriteExercises(userId: String): Result<List<ExerciseDto>>
    suspend fun addFavoriteExercise(userId: String, exerciseId: String): Result<Unit>
    suspend fun removeFromFav(userId: String, exerciseId: String): Result<Unit>
}