package com.kwasowski.sportslife.ui.main.calendarDay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.DayDto
import com.kwasowski.sportslife.data.calendar.TrainingState
import com.kwasowski.sportslife.domain.calendar.GetSingleDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CalendarDayViewModel(private val getSingleDayUseCase: GetSingleDayUseCase) : ViewModel() {

    private val mutableState = MutableStateFlow<CalendarDayState>(CalendarDayState.Default)
    val uiState: StateFlow<CalendarDayState> = mutableState.asStateFlow()

    fun getDay(dayID: String) {
        viewModelScope.launch {
            getSingleDayUseCase.execute(dayID).let {
                when (it) {
                    is Result.Failure -> Timber.e(it.exception) // TODO: showToast error
                    is Result.Success -> onSuccessGetDay(it.data)
                }
            }
        }
    }

    private fun onSuccessGetDay(data: DayDto) {
        val scheduledTrainings = data.trainingList.filter { it.state == TrainingState.SCHEDULED }
        val completedTrainings = data.trainingList.filter { it.state == TrainingState.COMPLETED }

        mutableState.value = CalendarDayState.OnSuccessGetDay(scheduledTrainings, completedTrainings)
    }
}