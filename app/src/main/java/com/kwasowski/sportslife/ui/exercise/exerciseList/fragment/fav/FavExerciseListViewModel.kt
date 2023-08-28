package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.domain.exercise.GetFavExercisesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class FavExerciseListViewModel(
    private val getFavExercisesUseCase: GetFavExercisesUseCase
): ViewModel() {
    private val mutableState = MutableStateFlow<FavExerciseListState>(FavExerciseListState.Default)
    val uiState: StateFlow<FavExerciseListState> = mutableState.asStateFlow()

    private val exercises = mutableListOf<ExerciseDto>()

    fun getExerciseList() {
        Timber.d("getExerciseList()")
        viewModelScope.launch {
            when (val result = getFavExercisesUseCase.execute()) {
                is Result.Failure -> mutableState.value = FavExerciseListState.OnFailure
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        exercises.clear()
                        mutableState.value = FavExerciseListState.OnSuccessGetEmptyList
                    } else {
                        exercises.clear()
                        exercises.addAll(result.data)
                        mutableState.value = FavExerciseListState.OnSuccessGetExerciseList
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
        mutableState.value = FavExerciseListState.OnFilteredExercises(filteredList)
    }
}