package com.kwasowski.sportslife.ui.main.appBarDays

import com.kwasowski.sportslife.utils.TimeLocationTag
import java.time.LocalDate
import java.util.Date

data class Day(
    val name: String,
    val number: Int,
    val month: Int,
    val year: Int,
    var type: DayType,
    var isToday: Boolean = false,
    val date: Date,
)

fun MutableList<Day>.findByCalendarDate(number: Int, month: Int, year: Int): Day {
    return this.filter { it.year == year }.filter { it.month == month }
        .filter { it.number == number }[0]
}

fun Day.compareToActualTime(): String {
    val currentDate = LocalDate.now()
    val localeDate = LocalDate.of(year, month + 1, number)
    return when {
        localeDate.isAfter(currentDate) -> TimeLocationTag.AFTER
        localeDate.isBefore(currentDate) -> TimeLocationTag.BEFORE
        else -> TimeLocationTag.ACTUAL
    }
}

enum class DayType(val value: Int) {
    DEFAULT(0),
    ACTIVE(1),
    CURRENT(2);
}
