package com.kwasowski.sportslife.ui.main

import com.kwasowski.sportslife.data.model.Day

sealed class MainViewState {
    object Default : MainViewState()
    class OnInitDays(val days: List<Day>) : MainViewState()
    class OnDayItemClick(val days: List<Day>) : MainViewState()
}