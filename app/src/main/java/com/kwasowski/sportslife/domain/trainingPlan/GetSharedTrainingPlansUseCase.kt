package com.kwasowski.sportslife.domain.trainingPlan

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanRepository

class GetSharedTrainingPlansUseCase(private val trainingPlanRepository: TrainingPlanRepository) {
    suspend fun execute(): Result<List<TrainingPlanDto>> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(Exception("User is not logged in"))
        return trainingPlanRepository.getSharedTrainingPlans(uid)
    }
}