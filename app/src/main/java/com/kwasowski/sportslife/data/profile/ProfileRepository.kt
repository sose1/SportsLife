package com.kwasowski.sportslife.data.profile

interface ProfileRepository {
    suspend fun saveProfile(uid: String, profile: Profile)
    suspend fun getProfile(uid: String): Profile?
}