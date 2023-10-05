package com.kwasowski.sportslife.domain.profile

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.profile.Profile
import com.kwasowski.sportslife.data.profile.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend fun execute(): Result<Profile> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return when (val result = profileRepository.getProfile(uid)) {
            is Result.Failure -> if (result.exception is NullPointerException) Result.Success(
                Profile()
            ) else result

            is Result.Success -> result
        }
    }
}