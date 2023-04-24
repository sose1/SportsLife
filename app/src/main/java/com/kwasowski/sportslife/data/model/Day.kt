package com.kwasowski.sportslife.data.model

import java.util.Date

data class Day(
    val name: String,
    val number: String,
    val month: Int,
    val year: Int,
    var type: DayType,
    var position: Int = 0,
    var isToday: Boolean = false,
    val date: Date = Date()
)

enum class DayType(val value: Int) {
    DEFAULT(0),
    ACTIVE(1),
    CURRENT(2);
}
