package com.kwasowski.sportslife.ui.main.calendarDay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.domain.calendar.GetSingleDayUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

class CalendarDayViewModel(private val getSingleDayUseCase: GetSingleDayUseCase) : ViewModel() {
    init {
        Timber.d("Init CalendarDayViewModel")
    }

    fun getDay(dayID: String) {
        viewModelScope.launch {
            getSingleDayUseCase.execute(dayID).let {
                when (it) {
                    is Result.Failure -> Timber.e(it.exception) // TODO: showToast error
                    is Result.Success -> Timber.d("${it.data}") // TODO: podzielić liste treningów na dwie osobne listy pofiltrowana na scheduled i completed
                }
            }
        }
    }

}