package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own

import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto

sealed class OwnTrainingPlansState {
    object Default : OwnTrainingPlansState()
    object OnFailure : OwnTrainingPlansState()
    object OnSuccessGetEmptyList : OwnTrainingPlansState()
    object OnSuccessGetTrainingPlans : OwnTrainingPlansState()
    object OnSuccessDeleteTrainingPlan : OwnTrainingPlansState()

    class OnFilteredTrainingPlans(val filteredList: List<TrainingPlanDto>) : OwnTrainingPlansState()
}