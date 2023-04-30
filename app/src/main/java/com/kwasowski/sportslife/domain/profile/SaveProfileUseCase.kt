package com.kwasowski.sportslife.domain.profile

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.profile.Gender
import com.kwasowski.sportslife.data.profile.Profile
import com.kwasowski.sportslife.data.profile.ProfileRepository
import kotlin.math.pow
import kotlin.math.round

class SaveProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend fun execute(gender: Gender, height: Int, weight: Int): Double {
        val bmi = calculateBMI(height, weight)
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null)
            profileRepository.saveProfile(uid, Profile(gender, height, weight, bmi))

        return bmi
    }

    private fun calculateBMI(height: Int, weight: Int): Double {
        return round(((weight / height.toDouble().pow(2)) * 10000) * 100) / 100
    }
}