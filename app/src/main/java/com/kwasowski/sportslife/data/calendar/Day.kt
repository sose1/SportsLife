package com.kwasowski.sportslife.data.calendar

import java.io.Serializable

data class Day @JvmOverloads constructor(
    val number: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    var trainingList: List<Training> = emptyList(),
)

data class DayDto @JvmOverloads constructor(
    val id: String = "",
    val number: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    var trainingList: List<Training> = emptyList(),
) : Serializable

fun MutableList<DayDto>.findByCalendarDate(number: Int, month: Int, year: Int): DayDto? {
    return try {
        this.filter { it.year == year }.filter { it.month == month }
            .filter { it.number == number }[0]
    } catch (e: IndexOutOfBoundsException) {
        null
    }
}