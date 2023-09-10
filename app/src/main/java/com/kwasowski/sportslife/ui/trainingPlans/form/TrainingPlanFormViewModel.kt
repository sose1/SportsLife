package com.kwasowski.sportslife.ui.trainingPlans.form

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.domain.trainingPlan.SaveTrainingPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingPlanFormViewModel(
    private val saveTrainingPlanUseCase: SaveTrainingPlanUseCase
) : ViewModel() {
    private val mutableState =
        MutableStateFlow<TrainingPlanFormState>(TrainingPlanFormState.Default)
    val uiState: StateFlow<TrainingPlanFormState> = mutableState.asStateFlow()

    val id = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()


    enum class InputLengthLimit(val value: Int) {
        NAME(50),
        DESCRIPTION(350)
    }

    fun saveTrainingPlan() {
        Timber.d("saveTrainingPlan")
        if (validateInputData()) {
            viewModelScope.launch {
                val result = saveTrainingPlanUseCase.execute(
                    id = id.value,
                    name = name.value,
                    description = description.value
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
}