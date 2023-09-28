package com.kwasowski.sportslife.domain.trainingPlan

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanRepository

class GetTrainingPlanUseCase(private val trainingPlanRepository: TrainingPlanRepository) {
    suspend fun execute(trainingPlanId: String): Result<TrainingPlanDto> {
        Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return when (val result = trainingPlanRepository.getTrainingPlanById(trainingPlanId)) {
            is Result.Failure -> result
            is Result.Success -> result
        }
    }
}