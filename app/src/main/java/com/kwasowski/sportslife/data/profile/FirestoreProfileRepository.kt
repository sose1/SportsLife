package com.kwasowski.sportslife.data.profile

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreProfileRepository : ProfileRepository {
    private val path = "profiles"
    private val collection = Firebase.firestore.collection(path)

    override suspend fun saveProfile(uid: String, profile: Profile) {
        collection
            .document(uid)
            .set(profile, SetOptions.merge())
            .addOnSuccessListener { Timber.d("SUCCESS") }
            .addOnFailureListener { Timber.d("FAILURE") }
    }

    override suspend fun getProfile(uid: String): Profile? {
        return suspendCoroutine { continuation ->
            collection.document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val profile = documentSnapshot.toObject<Profile>()
                    continuation.resume(profile)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }

        }
    }
}