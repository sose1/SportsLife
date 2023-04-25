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
    val date: Date
)

fun MutableList<Day>.findByCalendarDate(number: String, month: Int, year: Int): Day {
    return this.filter { it.year == year }.filter { it.month == month }
        .filter { it.number == number }[0]
}

enum class DayType(val value: Int) {
    DEFAULT(0),
    ACTIVE(1),
    CURRENT(2);
}
