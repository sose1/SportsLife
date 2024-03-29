package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.domain.trainingPlan.DeleteOwnTrainingPlanUseCase
import com.kwasowski.sportslife.domain.trainingPlan.GetTrainingPlansByOwnerIdUseCase
import com.kwasowski.sportslife.domain.trainingPlan.SaveTrainingPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class OwnTrainingPlansViewModel(
    private val getTrainingPlansByOwnerIdUseCase: GetTrainingPlansByOwnerIdUseCase,
    private val deleteOwnTrainingPlanUseCase: DeleteOwnTrainingPlanUseCase,
    private val saveTrainingPlanUseCase: SaveTrainingPlanUseCase
) :
    ViewModel() {
    private val mutableState =
        MutableStateFlow<OwnTrainingPlansState>(OwnTrainingPlansState.Default)
    val uiState: StateFlow<OwnTrainingPlansState> = mutableState.asStateFlow()

    private val trainingPlans = mutableListOf<TrainingPlanDto>()

    fun getTrainingPlans() {
        Timber.d("getTrainingPlans()")
        viewModelScope.launch {
            when (val result = getTrainingPlansByOwnerIdUseCase.execute()) {
                is Result.Failure -> mutableState.value = OwnTrainingPlansState.OnFailure
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        trainingPlans.clear()
                        mutableState.value = OwnTrainingPlansState.OnSuccessGetEmptyList
                    } else {
                        trainingPlans.clear()
                        trainingPlans.addAll(result.data)
                        mutableState.value = OwnTrainingPlansState.OnSuccessGetTrainingPlans
                    }
                }
            }
        }
    }

    fun filterTrainingPlans(queryText: String) {
        val filteredList = mutableListOf<TrainingPlanDto>()
        trainingPlans.forEach {
            if (it.name.lowercase().contains(queryText.lowercase())) {
                filteredList.add(it)
            }
        }
        mutableState.value = OwnTrainingPlansState.OnFilteredTrainingPlans(filteredList)
    }

    fun deleteTrainingPlan(trainingPlan: TrainingPlanDto) {
        Timber.d("deleteTrainingPlan: $trainingPlan")
        viewModelScope.launch {
            when (deleteOwnTrainingPlanUseCase.execute(trainingPlan.id)) {
                is Result.Failure -> mutableState.value = OwnTrainingPlansState.OnFailure
                is Result.Success -> mutableState.value =
                    OwnTrainingPlansState.OnSuccessDeleteTrainingPlan
            }
        }
    }

    fun shareTrainingPlan(trainingPlan: TrainingPlanDto) {
        Timber.d("shareTrainingPlan: $trainingPlan")
        if (trainingPlan.id.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {
            val result = saveTrainingPlanUseCase.execute(
                id = trainingPlan.id,
                name = trainingPlan.name,
                description = trainingPlan.description,
                exerciseSeries = trainingPlan.exercisesSeries,
                shared = true
            )

            when (result) {
                is Result.Failure -> mutableState.value = OwnTrainingPlansState.OnFailure
                is Result.Success -> mutableState.value = OwnTrainingPlansState.OnSuccessShareTrainingPlan
            }
        }
    }
}