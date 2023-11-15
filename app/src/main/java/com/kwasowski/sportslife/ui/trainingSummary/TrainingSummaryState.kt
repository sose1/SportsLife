package com.kwasowski.sportslife.ui.trainingSummary

sealed class TrainingSummaryState {
    object Default : TrainingSummaryState()
    object OnSuccessSaveTraining : TrainingSummaryState()
}