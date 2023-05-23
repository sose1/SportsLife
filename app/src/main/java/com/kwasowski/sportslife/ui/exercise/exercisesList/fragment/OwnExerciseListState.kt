package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import com.kwasowski.sportslife.data.exercise.ExerciseDto

sealed class OwnExerciseListState {
    object Default : OwnExerciseListState()
    object OnSuccessGetEmptyList : OwnExerciseListState()
    object OnSuccessGetExerciseList : OwnExerciseListState()
    object OnFailure : OwnExerciseListState()
    object OnSuccessSharedExercise : OwnExerciseListState()
    object OnSuccessDeletingExercise : OwnExerciseListState()

    class OnFilteredExercises(val filteredList: List<ExerciseDto>) : OwnExerciseListState()

}