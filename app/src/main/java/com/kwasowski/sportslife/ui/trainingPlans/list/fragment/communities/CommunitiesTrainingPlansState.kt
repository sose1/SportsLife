package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities

import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto

sealed class CommunitiesTrainingPlansState {
    object Default : CommunitiesTrainingPlansState()

    object OnFailure : CommunitiesTrainingPlansState()
    object OnSuccessGetTrainingPlans : CommunitiesTrainingPlansState()
    class OnFilteredTrainingPlans(val filteredList: MutableList<TrainingPlanDto>) :
        CommunitiesTrainingPlansState()
}
