package com.kwasowski.sportslife.ui.activeTraining.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.data.calendar.TrainingState
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeriesInTraining
import com.kwasowski.sportslife.domain.calendar.GetTrainingUseCase
import com.kwasowski.sportslife.domain.calendar.SaveTrainingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ActiveTrainingViewModel(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val saveTrainingUseCase: SaveTrainingUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow<ActiveTrainingState>(ActiveTrainingState.Default)
    val uiState: StateFlow<ActiveTrainingState> = mutableState.asStateFlow()

    private lateinit var training: Training

    fun getTraining(dayId: String, trainingIdFromIntent: String) {
        Timber.d("getTraining")

        viewModelScope.launch {
            getTrainingUseCase.execute(dayId, trainingIdFromIntent).let {
                when (it) {
                    is Result.Failure -> {
                        Timber.d(it.exception)
                    }

                    is Result.Success -> {
                        training = it.data
                        mutableState.value = ActiveTrainingState.OnSuccessGetTraining(it.data)
                    }
                }
            }
        }
    }

    fun completeTraining(
        dayId: String,
        updatedExerciseSeries: List<ExerciseSeriesInTraining>,
        duration: String,
    ) {
        training.duration = duration
        training.trainingPlan?.exercisesSeries = updatedExerciseSeries
        training.state = TrainingState.COMPLETED

        viewModelScope.launch {
            when (val result = saveTrainingUseCase.execute(dayId, training.id, training)) {
                is Result.Success -> {
                    mutableState.value = ActiveTrainingState.OnSuccessSaveTraining(training)
                }

                is Result.Failure -> {
                    Timber.e("${result.exception}")
                }
            }
        }

    }
}