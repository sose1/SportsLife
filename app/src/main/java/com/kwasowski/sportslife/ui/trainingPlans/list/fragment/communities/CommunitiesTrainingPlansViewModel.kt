package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.domain.trainingPlan.GetSharedTrainingPlansUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CommunitiesTrainingPlansViewModel(private val getSharedTrainingPlansUseCase: GetSharedTrainingPlansUseCase) :
    ViewModel() {
    private val mutableState =
        MutableStateFlow<CommunitiesTrainingPlansState>(CommunitiesTrainingPlansState.Default)
    val uiState: StateFlow<CommunitiesTrainingPlansState> = mutableState.asStateFlow()

    private val trainingPlans = mutableListOf<TrainingPlanDto>()

    fun getTrainingPlans() {
        Timber.d("getTrainingPlans()")
        viewModelScope.launch {
            when (val result = getSharedTrainingPlansUseCase.execute()) {
                is Result.Failure -> mutableState.value = CommunitiesTrainingPlansState.OnFailure
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        trainingPlans.clear()
                        mutableState.value = CommunitiesTrainingPlansState.OnSuccessGetTrainingPlans
                    } else {
                        trainingPlans.clear()
                        trainingPlans.addAll(result.data)
                        mutableState.value = CommunitiesTrainingPlansState.OnSuccessGetTrainingPlans
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
        mutableState.value = CommunitiesTrainingPlansState.OnFilteredTrainingPlans(filteredList)
    }
}
