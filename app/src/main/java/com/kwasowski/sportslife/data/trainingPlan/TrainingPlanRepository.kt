package com.kwasowski.sportslife.data.trainingPlan

import com.kwasowski.sportslife.data.Result

interface TrainingPlanRepository {
    suspend fun saveTrainingPlan(id: String?, trainingPlan: TrainingPlan): Result<Unit>
    suspend fun getTrainingPlansByOwnerId(ownerId: String): Result<List<TrainingPlanDto>>
    suspend fun getSharedTrainingPlans(ownerId: String): Result<List<TrainingPlanDto>>
}