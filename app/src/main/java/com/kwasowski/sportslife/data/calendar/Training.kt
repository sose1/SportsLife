package com.kwasowski.sportslife.data.calendar

import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import java.util.UUID

data class Training @JvmOverloads constructor(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val state: TrainingState = TrainingState.SCHEDULED,
    val trainingPlan: TrainingPlanDto? = null
)

enum class TrainingState {
    SCHEDULED,
    COMPLETED
}