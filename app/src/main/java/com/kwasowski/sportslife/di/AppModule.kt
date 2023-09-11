package com.kwasowski.sportslife.di

import com.kwasowski.sportslife.data.category.CategoryRepository
import com.kwasowski.sportslife.data.category.CategorySharedPreferences
import com.kwasowski.sportslife.data.category.FirestoreCategoryRepository
import com.kwasowski.sportslife.data.exercise.ExerciseRepository
import com.kwasowski.sportslife.data.exercise.FirestoreExerciseRepository
import com.kwasowski.sportslife.data.profile.FirestoreProfileRepository
import com.kwasowski.sportslife.data.profile.ProfileRepository
import com.kwasowski.sportslife.data.settings.FirestoreSettingsRepository
import com.kwasowski.sportslife.data.settings.SettingsManager
import com.kwasowski.sportslife.data.settings.SettingsRepository
import com.kwasowski.sportslife.data.trainingPlan.FirestoreTrainingPlanRepository
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanRepository
import com.kwasowski.sportslife.domain.category.GetCategoriesUseCase
import com.kwasowski.sportslife.domain.exercise.AddToFavExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.DeleteOwnExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.GetExerciseByIdUseCase
import com.kwasowski.sportslife.domain.exercise.GetExerciseListByOwnerIdUseCase
import com.kwasowski.sportslife.domain.exercise.GetFavExercisesUseCase
import com.kwasowski.sportslife.domain.exercise.GetSharedExercisesUseCase
import com.kwasowski.sportslife.domain.exercise.RemoveFromFavExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.SaveExerciseUseCase
import com.kwasowski.sportslife.domain.exercise.ShareExerciseUseCase
import com.kwasowski.sportslife.domain.profile.GetProfileUseCase
import com.kwasowski.sportslife.domain.profile.SaveProfileUseCase
import com.kwasowski.sportslife.domain.settings.GetSettingsUseCase
import com.kwasowski.sportslife.domain.settings.SaveSettingsUseCase
import com.kwasowski.sportslife.domain.trainingPlan.GetSharedTrainingPlansUseCase
import com.kwasowski.sportslife.domain.trainingPlan.GetTrainingPlansByOwnerIdUseCase
import com.kwasowski.sportslife.domain.trainingPlan.SaveTrainingPlanUseCase
import com.kwasowski.sportslife.ui.exercise.details.ExerciseDetailsViewModel
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities.CommunitiesExerciseListViewModel
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav.FavExerciseListViewModel
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.own.OwnExerciseListViewModel
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormViewModel
import com.kwasowski.sportslife.ui.login.LoginViewModel
import com.kwasowski.sportslife.ui.main.MainViewModel
import com.kwasowski.sportslife.ui.profile.ProfileViewModel
import com.kwasowski.sportslife.ui.settings.SettingsViewModel
import com.kwasowski.sportslife.ui.trainingPlans.form.TrainingPlanFormViewModel
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities.CommunitiesTrainingPlansViewModel
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own.OwnTrainingPlansViewModel
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
    viewModelOf(::FavExerciseListViewModel)
    viewModelOf(::ExerciseFormViewModel)
    viewModelOf(::ExerciseDetailsViewModel)
    viewModelOf(::TrainingPlanFormViewModel)
    viewModelOf(::CommunitiesTrainingPlansViewModel)
    viewModelOf(::OwnTrainingPlansViewModel)

    single<ProfileRepository> { FirestoreProfileRepository() }
    single<SettingsRepository> { FirestoreSettingsRepository() }
    single<ExerciseRepository> { FirestoreExerciseRepository() }
    single<CategoryRepository> { FirestoreCategoryRepository() }
    single<TrainingPlanRepository> { FirestoreTrainingPlanRepository() }
    singleOf(::SettingsManager)
    singleOf(::CategorySharedPreferences)

    factoryOf(::SaveProfileUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::GetSettingsUseCase)
    factoryOf(::SaveExerciseUseCase)
    factoryOf(::GetExerciseListByOwnerIdUseCase)
    factoryOf(::ShareExerciseUseCase)
    factoryOf(::DeleteOwnExerciseUseCase)
    factoryOf(::GetSharedExercisesUseCase)
    factoryOf(::GetExerciseByIdUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::AddToFavExerciseUseCase)
    factoryOf(::GetFavExercisesUseCase)
    factoryOf(::RemoveFromFavExerciseUseCase)
    factoryOf(::SaveTrainingPlanUseCase)
    factoryOf(::GetTrainingPlansByOwnerIdUseCase)
    factoryOf(::GetSharedTrainingPlansUseCase)
}