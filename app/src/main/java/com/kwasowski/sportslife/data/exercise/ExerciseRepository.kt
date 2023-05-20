package com.kwasowski.sportslife.data.exercise

import com.kwasowski.sportslife.data.Result

interface ExerciseRepository {
    suspend fun saveExercise(id: String?, exercise: Exercise): Result<Unit>
}