package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.Exercise
import com.kwasowski.sportslife.domain.exercise.GetExerciseListByOwnerIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class OwnExerciseListViewModel(
    private val getExerciseListByOwnerIdUseCase: GetExerciseListByOwnerIdUseCase
) :
    ViewModel() {
    private val mutableState = MutableStateFlow<OwnExerciseListState>(OwnExerciseListState.Default)
    val uiState: StateFlow<OwnExerciseListState> = mutableState.asStateFlow()

    private val list = mutableListOf<Exercise>()

    init {
        getExerciseList()
    }

    fun getExerciseList(queryText: String = "") {
        viewModelScope.launch {
            when (val result = getExerciseListByOwnerIdUseCase.execute()) {
                is Result.Failure -> Timber.e(result.exception)
                is Result.Success -> {
                    list.clear()
                    list.addAll(result.data)
                    mutableState.value = OwnExerciseListState.OnSuccessGetExerciseList(filterExercises(queryText))
                }
            }
        }
    }

    fun filterExercises(queryText: String): List<Exercise> {
        val filteredList = mutableListOf<Exercise>()
        list.forEach {
            if (it.name.lowercase().contains(queryText.lowercase())) {
                filteredList.add(it)
            }
        }
        return filteredList
    }
}
