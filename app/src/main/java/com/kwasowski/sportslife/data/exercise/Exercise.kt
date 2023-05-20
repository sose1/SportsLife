package com.kwasowski.sportslife.data.exercise

data class Exercise(
    val name: String,
    val description: String,
    val category: String,
    val videoLink: String?,
    var isShared: Boolean = false,
    var ownerId: String
)
