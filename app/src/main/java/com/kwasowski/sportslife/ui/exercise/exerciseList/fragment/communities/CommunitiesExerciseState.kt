package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities

import com.kwasowski.sportslife.data.exercise.ExerciseDto

sealed class CommunitiesExerciseState {
    object Default: CommunitiesExerciseState()
    object OnSuccessGetExerciseList : CommunitiesExerciseState()
    object OnFailure : CommunitiesExerciseState()
    class OnFilteredExercises(val filteredList: List<ExerciseDto>) : CommunitiesExerciseState()
}