package com.kwasowski.sportslife.ui.activeTraining.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.domain.calendar.GetTrainingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ActiveTrainingViewModel(private val getTrainingUseCase: GetTrainingUseCase) : ViewModel() {
    private val mutableState = MutableStateFlow<ActiveTrainingState>(ActiveTrainingState.Default)
    val uiState: StateFlow<ActiveTrainingState> = mutableState.asStateFlow()

    fun onConfirmExit() {
        Timber.d("onConfirmExit")
    }

    fun getTraining(dayId: String, trainingIdFromIntent: String) {
        Timber.d("getTraining")

        viewModelScope.launch {
            getTrainingUseCase.execute(dayId, trainingIdFromIntent).let {
                when (it) {
                    is Result.Failure -> {
                        Timber.d(it.exception)
                    }

                    is Result.Success -> {
                        mutableState.value = ActiveTrainingState.OnSuccessGetTraining(it.data)
                    }
                }
            }
        }
    }
}