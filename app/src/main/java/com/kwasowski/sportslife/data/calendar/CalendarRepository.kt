package com.kwasowski.sportslife.data.calendar

import com.kwasowski.sportslife.data.Result

interface CalendarRepository {
    suspend fun getCalendarByOwnerId(ownerId: String): Result<Calendar>
    suspend fun getSingleDay(dayId: String, ownerId: String): Result<DayDto>
}