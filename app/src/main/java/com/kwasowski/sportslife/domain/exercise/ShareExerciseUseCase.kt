package com.kwasowski.sportslife.domain.exercise

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.Exercise
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.data.exercise.ExerciseRepository

class ShareExerciseUseCase(private val exerciseRepository: ExerciseRepository) {
    suspend fun execute(exercise: ExerciseDto): Result<Unit> {
        exercise.shared = true
        return Firebase.auth.currentUser?.uid?.let {
            exerciseRepository.saveExercise (
                id = exercise.id,
                exercise = Exercise (
                    name = exercise.name,
                    description = exercise.description,
                    category = exercise.category,
                    videoLink = exercise.videoLink,
                    shared = exercise.shared,
                    ownerId = exercise.ownerId,
                    creationDate = exercise.creationDate!!
                )
            )
        } ?: Result.Failure(Exception())
    }
}