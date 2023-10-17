package com.kwasowski.sportslife.ui.main.calendarDay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.CalendarRepository
import com.kwasowski.sportslife.data.calendar.Day
import com.kwasowski.sportslife.data.calendar.DayDto
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.data.calendar.TrainingState
import com.kwasowski.sportslife.domain.calendar.GetSingleDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CalendarDayViewModel(
    private val getSingleDayUseCase: GetSingleDayUseCase,
    private val calendarRepository: CalendarRepository,
) :
    ViewModel() {

    private val mutableState = MutableStateFlow<CalendarDayState>(CalendarDayState.Default)
    val uiState: StateFlow<CalendarDayState> = mutableState.asStateFlow()

    private lateinit var day: Day

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

        // TODO: mniej druciarstwo 
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

    fun saveDay(dayID: String, trainingList: List<Training>) {
        // TODO: jesli dayId jest puste to dodaj nowy obiekt
        // TODO: do otwarcia fragmentu w argumentach dodac dane z dnia (number, month, year), które klkniety został

        val newTrainingList = (day.trainingList) + trainingList
        viewModelScope.launch { // TODO: do use case wyciagnac
            Firebase.auth.uid?.let {
                calendarRepository.saveSingleDay(
                    dayID,
                    it,
                    Day(day.number, day.month, day.year, newTrainingList)
                )
            }
        }
    }
}