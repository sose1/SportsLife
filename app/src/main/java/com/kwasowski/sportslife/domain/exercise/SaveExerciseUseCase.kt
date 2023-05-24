package com.kwasowski.sportslife.domain.exercise

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.Exercise
import com.kwasowski.sportslife.data.exercise.ExerciseRepository

class SaveExerciseUseCase(private val exerciseRepository: ExerciseRepository) {
    suspend fun execute(id: String?, name: String?, description: String?, category: String?, videoLink: String?, shared: Boolean?): Result<Unit> {
        return Firebase.auth.currentUser?.uid?.let {
            exerciseRepository.saveExercise(
                id = id,
                exercise = Exercise(
                    name = name!!,
                    description = description!!,
                    category = category!!,
                    videoLink = videoLink,
                    shared = shared ?: false,
                    ownerId = it
                )
            )
        } ?: Result.Failure(Exception())
    }
}