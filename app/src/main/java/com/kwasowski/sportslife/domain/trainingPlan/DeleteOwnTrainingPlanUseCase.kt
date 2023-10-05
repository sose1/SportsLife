package com.kwasowski.sportslife.domain.trainingPlan

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanRepository

class DeleteOwnTrainingPlanUseCase(private val trainingPlanRepository: TrainingPlanRepository) {
    suspend fun execute(trainingPlanId: String): Result<Unit> {
        return Firebase.auth.currentUser?.uid?.let {
            trainingPlanRepository.deleteTrainingPlan(trainingPlanId)
        } ?: Result.Failure(Exception())
    }
}