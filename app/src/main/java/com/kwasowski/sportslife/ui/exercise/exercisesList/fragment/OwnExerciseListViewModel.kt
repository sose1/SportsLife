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

    private val exercises = mutableListOf<Exercise>()

    fun getExerciseList() {
        Timber.d("getExerciseList()")
        viewModelScope.launch {
            when (val result = getExerciseListByOwnerIdUseCase.execute()) {
                is Result.Failure -> mutableState.value = OwnExerciseListState.OnFailure
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        exercises.clear()
                        mutableState.value = OwnExerciseListState.OnSuccessGetEmptyList
                    } else {
                        exercises.clear()
                        exercises.addAll(result.data)
                        mutableState.value = OwnExerciseListState.OnSuccessGetExerciseList
                    }
                }
            }
        }
    }

    fun filterExercises(queryText: String) {
        val filteredList = mutableListOf<Exercise>()
        exercises.forEach {
            if (it.name.lowercase().contains(queryText.lowercase())) {
                filteredList.add(it)
            }
        }
        mutableState.value = OwnExerciseListState.OnFilteredExercises(filteredList)
    }
}
