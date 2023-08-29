package com.kwasowski.sportslife.ui.exercise.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.category.CategorySharedPreferences
import com.kwasowski.sportslife.data.category.getTranslation
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.domain.exercise.AddToFavExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.DeleteOwnExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.GetExerciseByIdUseCase
import com.kwasowski.sportslife.domain.exercise.SaveExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.ShareExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseDetailsViewModel(
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val deleteOwnExerciseUseCase: DeleteOwnExerciseUseCase,
    private val shareExerciseUseCase: ShareExerciseUseCase,
    private val saveExerciseUseCase: SaveExerciseUseCase,
    private val addToFavExerciseUseCase: AddToFavExerciseUseCase,
    private val categorySharedPreferences: CategorySharedPreferences
) : ViewModel() {
    private val mutableState = MutableStateFlow<ExerciseDetailsState>(ExerciseDetailsState.Default)
    val uiState: StateFlow<ExerciseDetailsState> = mutableState.asStateFlow()

    val id = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val category = MutableLiveData<String>()
    val videoLink = MutableLiveData<String>()
    val exerciseDtoMutableLiveData = MutableLiveData<ExerciseDto>()

    fun getExercise(exerciseId: String?) {

        viewModelScope.launch {
            if (!exerciseId.isNullOrBlank())
                getExerciseByIdUseCase.execute(exerciseId).let {
                    when (it) {
                        is Result.Failure -> mutableState.value = ExerciseDetailsState.OnFailure
                        is Result.Success -> {
                            id.value = it.data.id
                            name.value = it.data.name
                            description.value = it.data.description
                            category.value = categorySharedPreferences
                                .getById(it.data.category)?.getTranslation()
                            videoLink.value = it.data.videoLink ?: ""
                            exerciseDtoMutableLiveData.value = it.data
                            mutableState.value = ExerciseDetailsState.OnSuccessGet
                        }
                    }
                }
        }
    }

    fun onVideoClick() {
        mutableState.value = ExerciseDetailsState.OnVideoClick(videoLink.value)
    }

    fun setDefaultState() {
        mutableState.value = ExerciseDetailsState.Default
    }

    fun deleteExercise() {
        Timber.d("deleteExercise: ${exerciseDtoMutableLiveData.value}")
        viewModelScope.launch {
            when (id.value?.let { deleteOwnExerciseUseCase.execute(it) }) {
                is Result.Failure -> mutableState.value = ExerciseDetailsState.OnFailure
                is Result.Success -> mutableState.value =
                    ExerciseDetailsState.OnSuccessDelete

                null -> Unit
            }
        }
    }

    fun shareExercise() {
        Timber.d("shareExercise | $exerciseDtoMutableLiveData")
        viewModelScope.launch {
            when (exerciseDtoMutableLiveData.value?.let { shareExerciseUseCase.execute(it) }) {
                is Result.Failure -> mutableState.value = ExerciseDetailsState.OnFailure
                is Result.Success -> mutableState.value =
                    ExerciseDetailsState.OnSuccessSharedExercise

                null -> Unit
            }
        }
    }

    fun copyToOwn() {
        Timber.d("copyToOwn() | $exerciseDtoMutableLiveData")
        viewModelScope.launch {
            when (saveExerciseUseCase.execute(
                id = null,
                name = exerciseDtoMutableLiveData.value?.name,
                description = exerciseDtoMutableLiveData.value?.description,
                category = exerciseDtoMutableLiveData.value?.category,
                videoLink = exerciseDtoMutableLiveData.value?.videoLink,
                shared = false
            )) {
                is Result.Failure -> mutableState.value = ExerciseDetailsState.OnFailure
                is Result.Success -> mutableState.value = ExerciseDetailsState.OnSuccessCopy
            }
        }
    }

    fun addToFav() {
        Timber.d("addToFav: $exerciseDtoMutableLiveData")

        viewModelScope.launch {
            when (addToFavExerciseUseCase.execute(exerciseDtoMutableLiveData.value!!.id)) {
                is Result.Failure -> mutableState.value = ExerciseDetailsState.OnFailure
                is Result.Success -> mutableState.value =
                    ExerciseDetailsState.OnSuccessAddToFav
            }
        }
    }
}