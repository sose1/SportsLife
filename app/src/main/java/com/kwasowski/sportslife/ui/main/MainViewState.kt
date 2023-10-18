package com.kwasowski.sportslife.ui.main

import com.google.android.material.datepicker.CalendarConstraints
import com.kwasowski.sportslife.ui.main.appBarDays.Day

sealed class MainViewState {
    object Default : MainViewState()
    object OnIndexOutOfBoundsException : MainViewState()
    class OnInitDays(val days: List<Day>, val todayIndex: Int) : MainViewState()
    class OnDaysListUpdate(val days: List<Day>) : MainViewState()
    class OnDayItemClick(
        val indexOf: Int,
        val dayId: String,
        val timeLocation: String,
        val number: Int,
        val month: Int,
        val year: Int,
    ) : MainViewState()

    class OnDataPickerOpen(val constraints: CalendarConstraints) : MainViewState()
    class OnTitleChange(val month: CharSequence, val year: Int) : MainViewState()
    class OnGetSettings(val language: String) : MainViewState()
    class ClickSelectedDay(val id: String) : MainViewState()

    object OnLogout : MainViewState()
    object Loading : MainViewState()
    object OnCalendarError : MainViewState()
}