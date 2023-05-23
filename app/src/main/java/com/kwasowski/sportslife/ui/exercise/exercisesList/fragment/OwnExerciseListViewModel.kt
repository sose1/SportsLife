package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.domain.exercise.DeleteOwnExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.GetExerciseListByOwnerIdUseCase
import com.kwasowski.sportslife.domain.exercise.ShareExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class OwnExerciseListViewModel(
    private val getExerciseListByOwnerIdUseCase: GetExerciseListByOwnerIdUseCase,
    private val shareExerciseUseCase: ShareExerciseUseCase,
    private val deleteOwnExerciseUseCase: DeleteOwnExerciseUseCase
) :
    ViewModel() {
    private val mutableState = MutableStateFlow<OwnExerciseListState>(OwnExerciseListState.Default)
    val uiState: StateFlow<OwnExerciseListState> = mutableState.asStateFlow()

    private val exercises = mutableListOf<ExerciseDto>()

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
        val filteredList = mutableListOf<ExerciseDto>()
        exercises.forEach {
            if (it.name.lowercase().contains(queryText.lowercase())) {
                filteredList.add(it)
            }
        }
        mutableState.value = OwnExerciseListState.OnFilteredExercises(filteredList)
    }

    fun shareExercise(exercise: ExerciseDto) {
        Timber.d("shareExercise | $exercise")
        viewModelScope.launch {
            when (shareExerciseUseCase.execute(exercise)) {
                is Result.Failure -> mutableState.value = OwnExerciseListState.OnFailure
                is Result.Success -> mutableState.value = OwnExerciseListState.OnSuccessSharedExercise
            }
        }
    }

    fun deleteExercise(exercise: ExerciseDto) {
        Timber.d("deleteExercise: $exercise")
        viewModelScope.launch {
            when(deleteOwnExerciseUseCase.execute(exercise.id)) {
                is Result.Failure -> mutableState.value = OwnExerciseListState.OnFailure
                is Result.Success -> mutableState.value = OwnExerciseListState.OnSuccessDeletingExercise
            }
        }
    }
}
