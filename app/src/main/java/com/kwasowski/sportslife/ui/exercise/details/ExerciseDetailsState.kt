package com.kwasowski.sportslife.ui.exercise.details

sealed class ExerciseDetailsState {
    class OnVideoClick(val videoLink: String?) : ExerciseDetailsState()

    object Default : ExerciseDetailsState()
    object OnFailure : ExerciseDetailsState()
    object OnSuccessGet : ExerciseDetailsState()
    object OnSuccessDelete : ExerciseDetailsState()
    object OnSuccessSharedExercise : ExerciseDetailsState()
    object OnSuccessCopy : ExerciseDetailsState()
    object OnSuccessAddToFav : ExerciseDetailsState()
}
