package com.kwasowski.sportslife.data.exercise

import java.util.Date

data class ExerciseDto @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val videoLink: String? = "",
    var shared: Boolean = false,
    var ownerId: String = "",
    val creationDate: Date?
)