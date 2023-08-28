package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav

import com.kwasowski.sportslife.data.exercise.ExerciseDto

sealed class FavExerciseListState {
    class OnFilteredExercises(val filteredList: MutableList<ExerciseDto>) : FavExerciseListState()


    object Default : FavExerciseListState()
    object OnFailure : FavExerciseListState()
    object OnSuccessGetEmptyList : FavExerciseListState()
    object OnSuccessGetExerciseList : FavExerciseListState()
}