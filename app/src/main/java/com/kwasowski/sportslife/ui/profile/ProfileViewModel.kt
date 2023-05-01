package com.kwasowski.sportslife.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.profile.Gender
import com.kwasowski.sportslife.domain.profile.GetProfileUseCase
import com.kwasowski.sportslife.domain.profile.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {
    private val mutableState = MutableStateFlow<ProfileViewState>(ProfileViewState.Default)
    val uiState: StateFlow<ProfileViewState> = mutableState.asStateFlow()

    private var height: Int = 70
    private var weight: Int = 170
    private var bmi: Double = 0.0
    private var gender: Gender = Gender.WOMAN

    suspend fun onGenderChanged(newGender: Gender) {
        Timber.d("$newGender")
        gender = newGender
        viewModelScope.launch {
            saveProfileUseCase.execute(gender, height, weight)
        }
    }

    suspend fun updateProfile(newHeight: Int = height, newWeight: Int = weight) {
        height = newHeight
        weight = newWeight

        viewModelScope.launch {
            mutableState.value =
                ProfileViewState.OnBMIChanged(saveProfileUseCase.execute(gender, height, weight))
        }
    }

    suspend fun getProfile() {
        viewModelScope.launch {
            getProfileUseCase.execute().let {
                when (it) {
                    is Result.Failure -> mutableState.value = ProfileViewState.OnGetProfileError
                    is Result.Success -> {
                        gender = it.data.gender
                        height = it.data.height
                        weight = it.data.weight
                        bmi = it.data.bmi
                        mutableState.value =
                            ProfileViewState.OnGetProfile(gender, height, weight, bmi)
                    }
                }
            }
        }
    }
}