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
    val exerciseName: String = "",
    val position: Int = 0,
    val series: List<Series> = emptyList()
)

data class Series @JvmOverloads constructor(
    val value: Int = 0,
    val repeats: Int = 0
)