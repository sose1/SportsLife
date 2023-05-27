package com.kwasowski.sportslife.data.exercise

import com.kwasowski.sportslife.data.Result

interface ExerciseRepository {
    suspend fun saveExercise(id: String?, exercise: Exercise): Result<Unit>
    suspend fun getExerciseListByOwnerId(ownerId: String): Result<List<ExerciseDto>>
    suspend fun deleteExercise(id: String): Result<Unit>
    suspend fun getSharedExercises(ownerId: String): Result<List<ExerciseDto>>
}