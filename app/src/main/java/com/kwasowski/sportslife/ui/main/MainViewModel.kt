package com.kwasowski.sportslife.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val mutableState = MutableStateFlow<MainViewState>(MainViewState.Default)
    val uiState: StateFlow<MainViewState> = mutableState.asStateFlow()

}