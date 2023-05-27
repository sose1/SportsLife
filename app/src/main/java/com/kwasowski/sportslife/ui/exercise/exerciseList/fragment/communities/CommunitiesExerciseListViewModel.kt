package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.domain.exercise.GetSharedExercisesUseCase
import com.kwasowski.sportslife.domain.exercise.SaveExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CommunitiesExerciseListViewModel(
    private val getSharedExercisesUseCase: GetSharedExercisesUseCase,
    private val saveExerciseUseCase: SaveExerciseUseCase
) : ViewModel() {
    private val mutableState =
        MutableStateFlow<CommunitiesExerciseState>(CommunitiesExerciseState.Default)
    val uiState: StateFlow<CommunitiesExerciseState> = mutableState.asStateFlow()

    private val exercises = mutableListOf<ExerciseDto>()

    fun getExerciseList() {
        Timber.d("getExerciseList()")
        viewModelScope.launch {
            when (val result = getSharedExercisesUseCase.execute()) {
                is Result.Failure -> mutableState.value = CommunitiesExerciseState.OnFailure
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        exercises.clear()
                        mutableState.value = CommunitiesExerciseState.OnSuccessGetExerciseList
                    } else {
                        exercises.clear()
                        exercises.addAll(result.data)
                        mutableState.value = CommunitiesExerciseState.OnSuccessGetExerciseList
                    }
                }
            }
        }
    }

    fun filterExercises(queryText: String) {
        val filteredList = mutableListOf<ExerciseDto>()
        exercises.forEach {
            if (it.name.lowercase().contains(queryText.lowercase())) {
                filteredList.add(it)
            }
        }
        mutableState.value = CommunitiesExerciseState.OnFilteredExercises(filteredList)
    }

    fun copyToOwn(exercise: ExerciseDto) {
        Timber.d("copyToOwn() | $exercise")
        viewModelScope.launch {
            when (saveExerciseUseCase.execute(
                id = null,
                name = exercise.name,
                description = exercise.description,
                category = exercise.category,
                videoLink = exercise.videoLink,
                shared = false
            )) {
                is Result.Failure -> mutableState.value = CommunitiesExerciseState.OnFailure
                is Result.Success -> mutableState.value = CommunitiesExerciseState.OnSuccessCopy
            }
        }
    }
}