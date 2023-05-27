package com.kwasowski.sportslife.domain.exercise

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.data.exercise.ExerciseRepository

class GetSharedExercisesUseCase(private val exerciseRepository: ExerciseRepository) {
    suspend fun execute(): Result<List<ExerciseDto>> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return when(val result = exerciseRepository.getSharedExercises(uid)) {
            is Result.Failure -> result
            is Result.Success -> result
        }
    }
}