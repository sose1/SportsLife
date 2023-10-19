package com.kwasowski.sportslife.data.calendar

data class Training @JvmOverloads constructor(
    val name: String = "",
    val trainingPlanId: String = "",
    val state: TrainingState = TrainingState.SCHEDULED,
    val numberOfExercises: Int = 0
)

enum class TrainingState {
    SCHEDULED,
    COMPLETED
}