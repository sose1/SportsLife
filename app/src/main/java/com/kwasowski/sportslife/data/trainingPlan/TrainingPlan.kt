package com.kwasowski.sportslife.data.trainingPlan

import java.util.Date


data class TrainingPlan @JvmOverloads constructor(
    val name: String = "",
    val description: String = "",
    val shared: Boolean = false,
    var ownerId: String = "",
    val updateDate: Date = Date(),
    val exercisesSeries: List<ExerciseSeries> = emptyList()
)

data class ExerciseSeries @JvmOverloads constructor(
    val originalId: String = "",
    val exerciseName: String = "",
    val units: String = "",
    var series: List<Series> = emptyList()
)

data class Series @JvmOverloads constructor(
    var value: Int = 0,
    var repeats: Int = 0
)