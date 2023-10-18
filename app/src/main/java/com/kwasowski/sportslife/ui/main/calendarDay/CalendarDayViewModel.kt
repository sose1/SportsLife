package com.kwasowski.sportslife.ui.main.calendarDay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.Day
import com.kwasowski.sportslife.data.calendar.DayDto
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.data.calendar.TrainingState
import com.kwasowski.sportslife.domain.calendar.GetSingleDayUseCase
import com.kwasowski.sportslife.domain.calendar.SaveSingleDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CalendarDayViewModel(
    private val getSingleDayUseCase: GetSingleDayUseCase,
    private val saveSingleDayUseCase: SaveSingleDayUseCase,
) :
    ViewModel() {


    private val mutableState = MutableStateFlow<CalendarDayState>(CalendarDayState.Default)
    val uiState: StateFlow<CalendarDayState> = mutableState.asStateFlow()

    private lateinit var day: Day

    fun getDay(dayID: String?) {
        if (dayID.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            getSingleDayUseCase.execute(dayID).let {
                when (it) {
                    is Result.Failure -> if (it.exception is NullPointerException) mutableState.value =
                        CalendarDayState.OnEmptyDay

                    is Result.Success -> onSuccessGetDay(it.data)
                }
            }
        }
    }

    private fun onSuccessGetDay(data: DayDto) {
        val scheduledTrainings = data.trainingList.filter { it.state == TrainingState.SCHEDULED }
        val completedTrainings = data.trainingList.filter { it.state == TrainingState.COMPLETED }

        day = Day(
            data.number,
            data.month,
            data.year,
            data.trainingList,
        )

        mutableState.value =
            CalendarDayState.OnSuccessGetDay(scheduledTrainings, completedTrainings)
    }

    fun setStateToDefault() {
        mutableState.value = CalendarDayState.Default
    }

    fun saveDay(dayID: String?, trainingList: List<Training>, number: Int, month: Int, year: Int) {
        if (dayID.isNullOrEmpty()) {
            day = Day(number, month, year, trainingList)
        } else {
            day.trainingList = (day.trainingList) + trainingList
        }

        viewModelScope.launch {
            when (val result = saveSingleDayUseCase.execute(dayID, day)) {
                is Result.Failure -> Timber.d("${result.exception}")
                is Result.Success -> {
                    mutableState.value = CalendarDayState.OnSuccessSave
                }
            }
        }
    }
}