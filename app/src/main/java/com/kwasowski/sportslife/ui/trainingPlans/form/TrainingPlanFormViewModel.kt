package com.kwasowski.sportslife.ui.trainingPlans.form

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.domain.trainingPlan.GetTrainingPlanUseCase
import com.kwasowski.sportslife.domain.trainingPlan.SaveTrainingPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingPlanFormViewModel(
    private val saveTrainingPlanUseCase: SaveTrainingPlanUseCase,
    private val getTrainingPlanUseCase: GetTrainingPlanUseCase
) : ViewModel() {
    private val mutableState =
        MutableStateFlow<TrainingPlanFormState>(TrainingPlanFormState.Default)
    val uiState: StateFlow<TrainingPlanFormState> = mutableState.asStateFlow()

    val id = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val exerciseSeries = MutableLiveData<List<ExerciseSeries>>()

    enum class InputLengthLimit(val value: Int) {
        NAME(50),
        DESCRIPTION(350)
    }

    fun getTrainingPlanById(trainingPlanId: String?) {
        Timber.d("getTrainingPlanById")
        if (trainingPlanId.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {
            getTrainingPlanUseCase.execute(trainingPlanId).let {
                when (it) {
                    is Result.Failure -> mutableState.value = TrainingPlanFormState.OnError
                    is Result.Success -> {
                        Timber.d("${it.data}")
                        id.value = it.data.id
                        name.value = it.data.name
                        description.value = it.data.description
                        exerciseSeries.value = it.data.exercisesSeries
                        mutableState.value = TrainingPlanFormState.OnSuccessGet
                    }
                }
            }
        }
    }

    fun trySaveTrainingPlan() {
        Timber.d("trySaveTrainingPlan")
        mutableState.value = TrainingPlanFormState.ReadExerciseSeries
    }

    fun saveTrainingPlan() {
        Timber.d("saveTrainingPlan")
        if (validateInputData()) {
            viewModelScope.launch {
                val result = saveTrainingPlanUseCase.execute(
                    id = id.value,
                    name = name.value,
                    description = description.value,
                    exerciseSeries = exerciseSeries.value ?: emptyList()
                )

                when (result) {
                    is Result.Failure -> mutableState.value = TrainingPlanFormState.OnError
                    is Result.Success -> mutableState.value = TrainingPlanFormState.OnSuccessSave
                }
            }
        }
    }

    private fun validateInputData(): Boolean {
        if (name.value.isNullOrBlank()) {
            mutableState.value = TrainingPlanFormState.OnNameEmptyError
            return false
        }

        if (name.value!!.length > InputLengthLimit.NAME.value) {
            mutableState.value = TrainingPlanFormState.OnNameLengthLimitError
            return false
        }

        if (description.value.isNullOrBlank()) {
            mutableState.value = TrainingPlanFormState.OnDescriptionEmptyError
            return false
        }

        if (description.value!!.length > InputLengthLimit.DESCRIPTION.value) {
            mutableState.value = TrainingPlanFormState.OnDescriptionLengthLimitError
            return false
        }

        return true
    }

    fun setStateToDefault() {
        mutableState.value = TrainingPlanFormState.Default
    }

    fun onSearchExerciseButtonClicked() {
        Timber.d("onSearchExerciseButtonClicked")
        mutableState.value = TrainingPlanFormState.OnSearchExerciseButtonClicked
    }
}