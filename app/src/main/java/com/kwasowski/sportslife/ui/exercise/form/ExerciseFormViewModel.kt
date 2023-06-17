package com.kwasowski.sportslife.ui.exercise.form

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.category.CategorySharedPreferences
import com.kwasowski.sportslife.data.category.getTranslation
import com.kwasowski.sportslife.domain.exercise.GetExerciseByIdUseCase
import com.kwasowski.sportslife.domain.exercise.SaveExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseFormViewModel(
    private val saveExerciseUseCase: SaveExerciseUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val categorySharedPreferences: CategorySharedPreferences) : ViewModel() {

    private val mutableState = MutableStateFlow<ExerciseFormState>(ExerciseFormState.Default)
    val uiState: StateFlow<ExerciseFormState> = mutableState.asStateFlow()

    val id = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val category = MutableLiveData<String>()
    val videoLink = MutableLiveData<String>()
    val shared = MutableLiveData<Boolean>()

    var categoriesNames = mutableListOf<String>()
    enum class InputLengthLimit(val value: Int) {
        NAME(50),
        DESCRIPTION(250)
    }

    init {
        categoriesNames.clear()
        categorySharedPreferences.getCategories().forEach { categoriesNames.add(it.getTranslation()) }
        categoriesNames.sort()
    }

    fun setStateToDefault() {
        mutableState.value = ExerciseFormState.Default
    }

    fun saveExercise() {
        if (validateInputData()) {
            viewModelScope.launch {
                val result = saveExerciseUseCase.execute(
                    id = id.value,
                    name = name.value,
                    description = description.value,
                    category = categorySharedPreferences.getCategoriesByTranslation(category.value)?.id,
                    videoLink = videoLink.value,
                    shared = shared.value
                )
                when (result) {
                    is Result.Failure -> mutableState.value = ExerciseFormState.OnError
                    is Result.Success -> mutableState.value = ExerciseFormState.OnSuccessSave
                }
            }
        }
    }

    private fun validateInputData(): Boolean {
        var isValid = true
        if (name.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnNameEmptyError
        } else if (name.value!!.length > InputLengthLimit.NAME.value) {
            isValid = false
            mutableState.value = ExerciseFormState.OnNameLengthLimitError
        }

        if (description.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnDescriptionEmptyError
        } else if (description.value!!.length > InputLengthLimit.DESCRIPTION.value) {
            isValid = false
            mutableState.value = ExerciseFormState.OnDescriptionLengthLimitError
        }

        if (category.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnCategoryEmptyError
        }

        if (!URLUtil.isValidUrl(videoLink.value) && !videoLink.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnVideoLinkInvalidUrlError
        }

        return isValid
    }

    fun setDefaultState() {
        mutableState.value = ExerciseFormState.Default
    }

    fun getExercise(exerciseId: String?) {

        viewModelScope.launch {
            if (!exerciseId.isNullOrBlank())
                getExerciseByIdUseCase.execute(exerciseId).let {
                    when (it) {
                        is Result.Failure -> mutableState.value = ExerciseFormState.OnFailureGet
                        is Result.Success -> {
                            Timber.d("${it.data}")
                            id.value = it.data.id
                            name.value = it.data.name
                            description.value = it.data.description
                            category.value = categorySharedPreferences
                                .getById(it.data.category)?.getTranslation()
                            videoLink.value = it.data.videoLink ?: ""
                            mutableState.value = ExerciseFormState.OnSuccessGet
                        }
                    }
                }
        }
    }
}