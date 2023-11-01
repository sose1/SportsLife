package com.kwasowski.sportslife.ui.activeTraining

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class ActiveTrainingViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(ActiveTrainingState.Default)
    val uiState: StateFlow<ActiveTrainingState> = mutableState.asStateFlow()


    init {
        Timber.d("ACTIVE TRAINING VIEW MODEL INITIALIZED")
    }

    fun onConfirmExit() {
        Timber.d("onConfirmExit")
    }
}