package com.kwasowski.sportslife.domain.calendar

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.Calendar
import com.kwasowski.sportslife.data.calendar.CalendarRepository

class GetCalendarByOwnerIdUseCase(private val calendarRepository: CalendarRepository) {
    suspend fun execute(): Result<Calendar> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return calendarRepository.getCalendarByOwnerId(uid)
    }
}