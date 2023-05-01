package com.kwasowski.sportslife.data.profile

import com.kwasowski.sportslife.data.Result

interface ProfileRepository {
    suspend fun saveProfile(uid: String, profile: Profile)
    suspend fun getProfile(uid: String): Result<Profile>
}