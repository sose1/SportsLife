package com.kwasowski.sportslife.ui.main.calendarDay

import com.kwasowski.sportslife.data.calendar.Training

sealed class CalendarDayState {
    class OnSuccessGetDay(val scheduledTrainings: List<Training>, val completedTrainings: List<Training>) : CalendarDayState()

    object Default : CalendarDayState()
}
