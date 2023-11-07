package com.kwasowski.sportslife.data.calendar

import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanInTraining
import java.util.UUID

data class Training @JvmOverloads constructor(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var duration: String? = "",
    var state: TrainingState = TrainingState.SCHEDULED,
    val trainingPlan: TrainingPlanInTraining? = null
)

enum class TrainingState {
    SCHEDULED,
    COMPLETED
}