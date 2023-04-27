package com.kwasowski.sportslife.ui.main

import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.extension.addDays
import com.kwasowski.sportslife.data.extension.getNarrowName
import com.kwasowski.sportslife.data.model.Day
import com.kwasowski.sportslife.data.model.DayType
import com.kwasowski.sportslife.data.model.findByCalendarDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Calendar
import java.util.Date

class MainViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<MainViewState>(MainViewState.Default)
    val uiState: StateFlow<MainViewState> = mutableState.asStateFlow()

    val auth = Firebase.auth

    private val daysList = mutableListOf<Day>()
    private val numberOfDays = 3652
    fun initializeDays() {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        var todayIndex = 0

        for (i in -numberOfDays..numberOfDays) {
            val day = currentDate.addDays(i)
            calendar.time = day

            val dayModel = Day(
                name = calendar.getNarrowName().uppercase(),
                number = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR),
                type = DayType.DEFAULT,
                date = day
            )

            if (DateUtils.isToday(day.time)) {
                dayModel.type = DayType.ACTIVE
                dayModel.isToday = true
                daysList.add(dayModel)
                todayIndex = daysList.size
            } else {
                daysList.add(dayModel)
            }
        }
        Timber.d("todayIndex: $todayIndex")
        mutableState.value = MainViewState.OnInitDays(daysList, todayIndex - 1)
    }

    fun onDayItemClick(day: Day) {
        Timber.d("onClick | Day: $day")
        //change today to current
        daysList.find { it.isToday }.apply {
            this?.type = DayType.CURRENT
        }

        //change active to default
        daysList.find { it.type == DayType.ACTIVE }.apply {
            this?.type = DayType.DEFAULT
        }

        //change clicked item to active
        try {
            daysList.findByCalendarDate(day.number, day.month, day.year).apply {
                this.type = DayType.ACTIVE
            }
        } catch (e: IndexOutOfBoundsException) {
            mutableState.value = MainViewState.OnIndexOutOfBoundsException
        }

        mutableState.value = MainViewState.OnDaysListUpdate(daysList)
        mutableState.value = MainViewState.OnDayItemClick(day, daysList.indexOf(day))
    }

    fun onDataPickerOpen() {
        val startFrom = Date().addDays(-numberOfDays).time
        val endTo = Date().addDays(numberOfDays).time
        val constraints = CalendarConstraints.Builder()
            .setStart(startFrom)
            .setEnd(endTo)
            .build()
        mutableState.value = MainViewState.OnDataPickerOpen(constraints)
    }

    fun onSelectedDateInDatePicker(timestamp: Long?) {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp!!)
        val number = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        try {
            onDayItemClick(
                daysList.findByCalendarDate(number, month, year)
            )
        } catch (e: IndexOutOfBoundsException) {
            mutableState.value = MainViewState.OnIndexOutOfBoundsException
        }
    }

    fun onScrollDays(itemPosition: Int) {
        val day = daysList[itemPosition]
        val month = DateFormat.format("LLLL", day.date)
        mutableState.value = MainViewState.OnTitleChange(month, day.year)
    }

    fun onLogoutClick() {
        auth.signOut()
        mutableState.value = MainViewState.OnLogout
    }
}