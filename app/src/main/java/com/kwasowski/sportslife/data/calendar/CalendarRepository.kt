package com.kwasowski.sportslife.data.calendar

import com.kwasowski.sportslife.data.Result

interface CalendarRepository {
    suspend fun getCalendarByOwnerId(ownerId: String): Result<Calendar>
    suspend fun getSingleDay(dayId: String, ownerId: String): Result<DayDto>
    suspend fun saveSingleDay(dayId: String?, ownerId:String, dayDto: Day): Result<Unit>
}