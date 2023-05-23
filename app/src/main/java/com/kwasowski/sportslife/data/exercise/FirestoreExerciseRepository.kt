package com.kwasowski.sportslife.data.exercise

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirestoreExerciseRepository : ExerciseRepository {
    private val path = "exercises"
    private val collection = Firebase.firestore.collection(path)

    override suspend fun saveExercise(id: String?, exercise: Exercise): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()

        val documentReference = if (id.isNullOrBlank()) {
            collection.document()
        } else {
            collection.document(id)
        }

        documentReference.set(exercise, SetOptions.merge())
            .addOnSuccessListener {
                deferred.complete(Result.Success(Unit))
            }
            .addOnFailureListener { exception ->
                deferred.complete(Result.Failure(exception))
            }

        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }

    override suspend fun deleteExercise(id: String): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()
        collection.document(id).delete()
            .addOnSuccessListener { deferred.complete(Result.Success(Unit)) }
            .addOnFailureListener { Result.Failure(it) }
        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }

    override suspend fun getExerciseListByOwnerId(ownerId: String): Result<List<ExerciseDto>> =
        suspendCoroutine { continuation ->
            collection.whereEqualTo("ownerId", ownerId)
                .orderBy(Exercise::creationDate.name, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val exerciseList = mutableListOf<ExerciseDto>()
                    querySnapshot.documents.forEach {
                        val exercise = it.toObject<Exercise>()
                        if (exercise != null) {
                            exerciseList.add(
                                ExerciseDto(
                                    it.id,
                                    exercise.name,
                                    exercise.description,
                                    exercise.category,
                                    exercise.videoLink,
                                    exercise.shared,
                                    exercise.ownerId,
                                    exercise.creationDate
                                )
                            )
                        }
                    }
                    continuation.resume(Result.Success(exerciseList))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Failure(exception))
                }
        }
}