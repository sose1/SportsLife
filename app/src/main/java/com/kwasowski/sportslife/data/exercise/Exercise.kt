package com.kwasowski.sportslife.data.exercise

import java.util.Date

data class Exercise @JvmOverloads constructor(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val videoLink: String? = "",
    var shared: Boolean = false,
    var ownerId: String = "",
    val creationDate: Date = Date()
)