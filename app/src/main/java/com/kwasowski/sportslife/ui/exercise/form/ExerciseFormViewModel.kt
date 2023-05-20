package com.kwasowski.sportslife.ui.exercise.form

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class ExerciseFormViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<ExerciseFormState>(ExerciseFormState.Default)
    val uiState: StateFlow<ExerciseFormState> = mutableState.asStateFlow()

    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val category = MutableLiveData<String>()
    val videoLink = MutableLiveData<String>()

    enum class InputLengthLimit(val value: Int) {
        NAME(50),
        DESCRIPTION(250)
    }

    fun setStateToDefault() {
        mutableState.value = ExerciseFormState.Default
    }

    fun saveExercise() {
        if (validateInputData()) {
            Timber.d(
                "saveExercise() | name: ${name.value} |" +
                        " description: ${description.value} |" +
                        " category: ${category.value} |" +
                        " videoLink: ${videoLink.value}"
            )
        }
    }

    private fun validateInputData(): Boolean {
        var isValid = true
        if (name.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnNameEmptyError
        } else if (name.value!!.length >= InputLengthLimit.NAME.value) {
            isValid = false
            mutableState.value = ExerciseFormState.OnNameLengthLimitError
        }

        if (description.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnDescriptionEmptyError
        } else if (description.value!!.length >= InputLengthLimit.DESCRIPTION.value) {
            isValid = false
            mutableState.value = ExerciseFormState.OnDescriptionLengthLimitError
        }

        if (category.value.isNullOrBlank()) {
            isValid = false
            mutableState.value = ExerciseFormState.OnCategoryEmptyError
        }
        if (!URLUtil.isValidUrl(videoLink.value)) {
            isValid = false
            mutableState.value = ExerciseFormState.OnVideoLinkInvalidUrlError
        }





        return isValid
    }
}