package com.kwasowski.sportslife.di

import com.kwasowski.sportslife.data.profile.FirestoreProfileRepository
import com.kwasowski.sportslife.data.profile.ProfileRepository
import com.kwasowski.sportslife.domain.profile.GetProfileUseCase
import com.kwasowski.sportslife.domain.profile.SaveProfileUseCase
import com.kwasowski.sportslife.ui.login.LoginViewModel
import com.kwasowski.sportslife.ui.main.MainViewModel
import com.kwasowski.sportslife.ui.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)

    single<ProfileRepository> {FirestoreProfileRepository()}

    factoryOf(::SaveProfileUseCase)
    factoryOf(::GetProfileUseCase)
}