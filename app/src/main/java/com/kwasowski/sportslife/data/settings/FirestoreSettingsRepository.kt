package com.kwasowski.sportslife.data.settings

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirestoreSettingsRepository : SettingsRepository {
    private val path = "settings"
    private val collection = Firebase.firestore.collection(path)

    override suspend fun saveSettings(uid: String, settings: Settings) {
        Timber.d("FirestoreSettingsRepository | saveSettings: $settings")
        collection
            .document(uid)
            .set(settings, SetOptions.merge())
            .addOnSuccessListener {
                Timber.d("saveSettings | SUCCESS")
            }
            .addOnFailureListener { Timber.d("saveSettings | FAILURE") }
    }

    override suspend fun getSettings(uid: String): Result<Settings> {
        return suspendCoroutine { continuation ->
            collection.document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val settings = documentSnapshot.toObject<Settings>()
                    if (settings != null)
                        continuation.resume(Result.Success(settings))
                    else
                        continuation.resume(Result.Failure(NullPointerException("Settings is null")))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Failure(exception))
                }
        }
    }
}