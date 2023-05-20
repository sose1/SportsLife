package com.kwasowski.sportslife.ui.exercise.form

sealed class ExerciseFormState {
    object Default : ExerciseFormState()
    object OnNameEmptyError: ExerciseFormState()
    object OnDescriptionEmptyError: ExerciseFormState()
    object OnCategoryEmptyError: ExerciseFormState()
    object OnVideoLinkInvalidUrlError: ExerciseFormState()
    object OnNameLengthLimitError: ExerciseFormState()
    object OnDescriptionLengthLimitError: ExerciseFormState()
}
