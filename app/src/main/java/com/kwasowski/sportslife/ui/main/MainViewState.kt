package com.kwasowski.sportslife.ui.main

import com.google.android.material.datepicker.CalendarConstraints
import com.kwasowski.sportslife.ui.main.appBarDays.Day

sealed class MainViewState {
    object Default : MainViewState()
    object OnIndexOutOfBoundsException : MainViewState()
    class OnInitDays(val days: List<Day>, val todayIndex: Int) : MainViewState()
    class OnDaysListUpdate(val days: List<Day>) : MainViewState()
    class OnDayItemClick(val day: Day, val indexOf: Int) : MainViewState()
    class OnDataPickerOpen(val constraints: CalendarConstraints) : MainViewState()
    class OnTitleChange(val month: CharSequence, val year: Int) : MainViewState()
    class OnGetSettings(val language: String) : MainViewState()
    object OnLogout : MainViewState()
}