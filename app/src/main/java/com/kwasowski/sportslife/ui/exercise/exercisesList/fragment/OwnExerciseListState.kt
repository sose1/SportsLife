package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import com.kwasowski.sportslife.data.exercise.Exercise

sealed class OwnExerciseListState {
    object Default : OwnExerciseListState()
    object OnSuccessGetEmptyList : OwnExerciseListState()
    object OnSuccessGetExerciseList : OwnExerciseListState()
    object OnFailure : OwnExerciseListState()
    class OnFilteredExercises(val filteredList: List<Exercise>) : OwnExerciseListState()

}