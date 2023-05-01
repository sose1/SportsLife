package com.kwasowski.sportslife.domain.profile

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.profile.Profile
import com.kwasowski.sportslife.data.profile.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend fun execute(): Result<Profile> {
        val uid = Firebase.auth.currentUser?.uid
        return uid?.let {
            when (val result = profileRepository.getProfile(uid)) {
                is Result.Failure -> {
                    when (result.exception) {
                        is NullPointerException -> Result.Success(Profile())
                        else -> result
                    }
                }

                is Result.Success -> result
            }
        } ?: Result.Failure(NullPointerException("UID is null"))
    }
}