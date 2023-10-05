package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.own

import com.kwasowski.sportslife.data.exercise.ExerciseDto

sealed class OwnExerciseListState {
    object Default : OwnExerciseListState()
    object OnSuccessGetEmptyList : OwnExerciseListState()
    object OnSuccessGetExerciseList : OwnExerciseListState()
    object OnFailure : OwnExerciseListState()
    object OnSuccessSharedExercise : OwnExerciseListState()
    object OnSuccessDeletingExercise : OwnExerciseListState()
    object OnSuccessAddToFav : OwnExerciseListState()

    class OnFilteredExercises(val filteredList: List<ExerciseDto>) : OwnExerciseListState()

}