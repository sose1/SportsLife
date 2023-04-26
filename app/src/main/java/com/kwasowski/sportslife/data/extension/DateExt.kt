package com.kwasowski.sportslife.data.extension

import java.util.Calendar
import java.util.Date

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return calendar.time
}
