package com.kwasowski.sportslife.domain.trainingPlan

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlan
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanRepository


class SaveTrainingPlanUseCase(private val trainingPlanRepository: TrainingPlanRepository) {
    suspend fun execute(
        id: String?,
        name: String?,
        description: String?,
    ): Result<Unit> {
        return Firebase.auth.currentUser?.uid?.let {
            trainingPlanRepository.saveTrainingPlan(
                id = id,
                trainingPlan = TrainingPlan(
                    name = name!!,
                    description = description!!,
                    ownerId = it,
                )
            )
        } ?: Result.Failure(Exception())
    }
}