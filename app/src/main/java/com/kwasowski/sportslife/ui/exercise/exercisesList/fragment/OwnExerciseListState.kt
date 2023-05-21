package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import com.kwasowski.sportslife.data.exercise.Exercise

sealed class OwnExerciseListState {
    object Default: OwnExerciseListState()
    class OnSuccessGetExerciseList(val exercises: List<Exercise>) : OwnExerciseListState()
}