package com.kwasowski.sportslife.ui.profile

import com.kwasowski.sportslife.data.profile.Gender

sealed class ProfileViewState {
    object Default : ProfileViewState()
    class OnBMIChanged(val BMI: Double) : ProfileViewState()
    class OnGetProfile(val gender: Gender, val height: Int, val weight: Int, val bmi: Double) :
        ProfileViewState()
}
