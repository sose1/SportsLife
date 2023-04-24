package com.kwasowski.sportslife.ui.main

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import com.kwasowski.sportslife.data.model.Day
import com.kwasowski.sportslife.data.model.DayType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<MainViewState>(MainViewState.Default)
    val uiState: StateFlow<MainViewState> = mutableState.asStateFlow()

    private val daysList = mutableListOf<Day>()
    private val numberOfDays = 3652

    fun initializeDays() {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        var todayIndex: Int = 0

        for (i in -numberOfDays..numberOfDays) {
            val day = currentDate.addDays(i)
            calendar.time = day

            val dayModel = Day(
                name = calendar.getNarrowName().uppercase(),
                number = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR),
                type = DayType.DEFAULT
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
        mutableState.value = MainViewState.OnInitDays(daysList, todayIndex)
    }

    fun onDayItemClick(day: Day) {
        Timber.d("onClick | Day: $day")

        //change today to current
        this.daysList.find { it.isToday }.apply {
            this?.type = DayType.CURRENT
        }

        //change active to default
        this.daysList.find { it.type == DayType.ACTIVE }.apply {
            this?.type = DayType.DEFAULT
        }

        //change clicked item to active
        this.daysList.find { it.position == day.position }.apply {
            this?.type = DayType.ACTIVE
        }

        mutableState.value = MainViewState.OnDaysListUpdate(daysList)
        mutableState.value = MainViewState.OnDayItemClick(day)
    }

    private fun Date.addDays(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    private fun Calendar.getNarrowName(): String =
        this.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.NARROW_FORMAT,
            Locale.getDefault()
        ) as String
}