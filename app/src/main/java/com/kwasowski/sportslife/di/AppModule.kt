package com.kwasowski.sportslife.di

import com.kwasowski.sportslife.data.profile.FirestoreProfileRepository
import com.kwasowski.sportslife.data.profile.ProfileRepository
import com.kwasowski.sportslife.data.settings.FirestoreSettingsRepository
import com.kwasowski.sportslife.data.settings.SettingsManager
import com.kwasowski.sportslife.data.settings.SettingsRepository
import com.kwasowski.sportslife.domain.profile.GetProfileUseCase
import com.kwasowski.sportslife.domain.profile.SaveProfileUseCase
import com.kwasowski.sportslife.domain.settings.GetSettingsUseCase
import com.kwasowski.sportslife.domain.settings.SaveSettingsUseCase
import com.kwasowski.sportslife.ui.login.LoginViewModel
import com.kwasowski.sportslife.ui.main.MainViewModel
import com.kwasowski.sportslife.ui.profile.ProfileViewModel
import com.kwasowski.sportslife.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SettingsViewModel)

    single<ProfileRepository> { FirestoreProfileRepository() }
    single<SettingsRepository> { FirestoreSettingsRepository() }
    singleOf(::SettingsManager)

    factoryOf(::SaveProfileUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::GetSettingsUseCase)
}