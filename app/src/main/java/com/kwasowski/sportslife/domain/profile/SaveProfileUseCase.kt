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
        Firebase.auth.currentUser?.uid?.let {
            profileRepository.saveProfile(
                uid = it,
                profile = Profile(
                    gender = gender,
                    height = height,
                    weight = weight,
                    bmi = bmi
                )
            )
        }
        return bmi
    }

    private fun calculateBMI(height: Int, weight: Int): Double {
        return round(((weight / height.toDouble().pow(2)) * 10000) * 100) / 100
    }
}