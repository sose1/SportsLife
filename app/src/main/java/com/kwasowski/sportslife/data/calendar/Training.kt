package com.kwasowski.sportslife.data.calendar

import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanInTraining
import java.io.Serializable
import java.util.UUID

data class Training @JvmOverloads constructor(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var duration: String? = "",
    var note: String? = "",
    var state: TrainingState = TrainingState.SCHEDULED,
    val trainingPlan: TrainingPlanInTraining? = null
): Serializable

enum class TrainingState {
    SCHEDULED,
    COMPLETED
}