package com.kwasowski.sportslife.ui.activeTraining.activity

import com.kwasowski.sportslife.data.calendar.Training

sealed class ActiveTrainingState {
    object Default : ActiveTrainingState()
    class OnSuccessSaveTraining(val training: Training) : ActiveTrainingState()
    class OnSuccessGetTraining(val training: Training) : ActiveTrainingState()
}
