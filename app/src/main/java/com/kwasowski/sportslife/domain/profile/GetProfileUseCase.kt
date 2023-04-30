package com.kwasowski.sportslife.domain.profile

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.profile.Profile
import com.kwasowski.sportslife.data.profile.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend fun execute(): Profile? {
        val uid = Firebase.auth.currentUser?.uid
        return uid?.let { profileRepository.getProfile(it) } ?: Profile()
    }
}