package com.kwasowski.sportslife.data.trainingPlan

import java.io.Serializable
import java.util.Date


data class TrainingPlanDto @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    var ownerId: String = "",
    val shared: Boolean = false,
    val updateDate: Date = Date(),
    val exercisesSeries: List<ExerciseSeries> = emptyList()
)

data class TrainingPlanInTraining @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    var ownerId: String = "",
    val shared: Boolean = false,
    val updateDate: Date = Date(),
    var exercisesSeries: List<ExerciseSeriesInTraining> = emptyList()
): Serializable
