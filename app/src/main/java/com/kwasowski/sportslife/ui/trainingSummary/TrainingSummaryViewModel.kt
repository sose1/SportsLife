package com.kwasowski.sportslife.ui.trainingSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.domain.calendar.SaveTrainingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingSummaryViewModel(private val saveTrainingUseCase: SaveTrainingUseCase) : ViewModel() {
    private val mutableState = MutableStateFlow<TrainingSummaryState>(TrainingSummaryState.Default)
    val uiState: StateFlow<TrainingSummaryState> = mutableState.asStateFlow()

    init {
        Timber.d("Xd")
    }

    fun saveTraining(
        dayId: String,
        training: Training,
    ) {

        // TODO: Stworzyc calego xml
        // TODO: podpiac xml do viewmodela

        training.note = "notatka"
        viewModelScope.launch {
            when (val result = saveTrainingUseCase.execute(dayId, training.id, training)) {
                is Result.Success -> {
                    mutableState.value = TrainingSummaryState.OnSuccessSaveTraining
                }

                is Result.Failure -> {
                    Timber.e("${result.exception}")
                }
            }
        }

    }
}