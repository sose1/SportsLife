package com.kwasowski.sportslife.ui.trainingPlans.form

sealed class TrainingPlanFormState {
    object Default : TrainingPlanFormState()
    object OnNameEmptyError : TrainingPlanFormState()
    object OnNameLengthLimitError : TrainingPlanFormState()
    object OnDescriptionEmptyError : TrainingPlanFormState()
    object OnDescriptionLengthLimitError : TrainingPlanFormState()
    object OnError : TrainingPlanFormState()
    object OnSuccessSave : TrainingPlanFormState()
    object onSearchExerciseButtonClicked : TrainingPlanFormState()
}