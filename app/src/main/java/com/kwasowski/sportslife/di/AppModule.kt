package com.kwasowski.sportslife.di

import com.kwasowski.sportslife.data.exercise.ExerciseRepository
import com.kwasowski.sportslife.data.exercise.FirestoreExerciseRepository
import com.kwasowski.sportslife.data.profile.FirestoreProfileRepository
import com.kwasowski.sportslife.data.profile.ProfileRepository
import com.kwasowski.sportslife.data.settings.FirestoreSettingsRepository
import com.kwasowski.sportslife.data.settings.SettingsManager
import com.kwasowski.sportslife.data.settings.SettingsRepository
import com.kwasowski.sportslife.domain.exercise.DeleteOwnExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.GetExerciseListByOwnerIdUseCase
import com.kwasowski.sportslife.domain.exercise.GetSharedExercisesUseCase
import com.kwasowski.sportslife.domain.exercise.SaveExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.ShareExerciseUseCase
import com.kwasowski.sportslife.domain.profile.GetProfileUseCase
import com.kwasowski.sportslife.domain.profile.SaveProfileUseCase
import com.kwasowski.sportslife.domain.settings.GetSettingsUseCase
import com.kwasowski.sportslife.domain.settings.SaveSettingsUseCase
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities.CommunitiesExerciseListViewModel
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.own.OwnExerciseListViewModel
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormViewModel
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
    viewModelOf(::OwnExerciseListViewModel)
    viewModelOf(::CommunitiesExerciseListViewModel)
    viewModelOf(::ExerciseFormViewModel)

    single<ProfileRepository> { FirestoreProfileRepository() }
    single<SettingsRepository> { FirestoreSettingsRepository() }
    single<ExerciseRepository> { FirestoreExerciseRepository() }
    singleOf(::SettingsManager)

    factoryOf(::SaveProfileUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::GetSettingsUseCase)
    factoryOf(::SaveExerciseUseCase)
    factoryOf(::GetExerciseListByOwnerIdUseCase)
    factoryOf(::ShareExerciseUseCase)
    factoryOf(::DeleteOwnExerciseUseCase)
    factoryOf(::GetSharedExercisesUseCase)
}