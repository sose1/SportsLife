package com.kwasowski.sportslife.di

import com.kwasowski.sportslife.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::LoginViewModel)
}