package com.kwasowski.sportslife.domain.exercise

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.ExerciseRepository

class DeleteOwnExerciseUseCase(private val exerciseRepository: ExerciseRepository) {
    suspend fun execute(exerciseId: String): Result<Unit> {
        return Firebase.auth.currentUser?.uid?.let {
            exerciseRepository.deleteExercise(exerciseId)
        } ?: Result.Failure(Exception())
    }
}