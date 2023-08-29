package com.kwasowski.sportslife.data.exercise

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
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
                .orderBy(Exercise::updateDate.name, Query.Direction.DESCENDING)
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
                                    exercise.updateDate
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

    override suspend fun getSharedExercises(ownerId: String): Result<List<ExerciseDto>> =
        suspendCoroutine { continuation ->
            collection.whereEqualTo(Exercise::shared.name, true)
                .whereNotEqualTo(Exercise::ownerId.name, ownerId)
                .orderBy(Exercise::ownerId.name)
                .orderBy(Exercise::name.name, Query.Direction.ASCENDING)
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
                                    exercise.updateDate
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

    override suspend fun getExerciseById(exerciseId: String): Result<ExerciseDto> =
        suspendCoroutine { continuation ->
            collection.document(exerciseId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject<Exercise>()?.let {
                        val exerciseDto = ExerciseDto(
                            id = documentSnapshot.id,
                            name = it.name,
                            description = it.description,
                            category = it.category,
                            videoLink = it.videoLink,
                            shared = it.shared,
                            ownerId = it.ownerId,
                            updateDate = it.updateDate
                        )
                        continuation.resume(Result.Success(exerciseDto))
                    } ?: Result.Failure(NullPointerException("Exercise not found"))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }

    override suspend fun getFavoriteExercises(userId: String): Result<List<ExerciseDto>> {

        val junctions = Firebase.firestore.collection("user_exercises")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val exerciseDocs = junctions.documents.filter {
            it.exists()
        }.map {
            collection.document(it.data?.get("exerciseId").toString()).get().await()
        }


        Timber.d("Count: ${exerciseDocs.count()}")
        exerciseDocs.filter { !it.exists() }.forEach {
            removeFromFav(userId, it.id)
        }
        Timber.d("Count: ${exerciseDocs.count()}")

        val exercisesList = mutableListOf<ExerciseDto>()
        exerciseDocs.filter { it.exists() }.map { documentSnapshot ->
            Timber.d("Doc: $documentSnapshot")
            documentSnapshot.toObject<Exercise>()?.let {
                exercisesList.add(
                    ExerciseDto(
                        id = documentSnapshot.id,
                        name = it.name,
                        description = it.description,
                        category = it.category,
                        videoLink = it.videoLink,
                        shared = it.shared,
                        ownerId = it.ownerId,
                        updateDate = it.updateDate
                    )
                )
            }
        }

        val sortedList = exercisesList.sortedByDescending { it.updateDate }
        return Result.Success(sortedList)
    }

    override suspend fun addFavoriteExercise(userId: String, exerciseId: String): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()
        Firebase.firestore.collection("user_exercises")
            .document("$userId-$exerciseId")
            .set(
                mapOf(
                    "userId" to userId,
                    "exerciseId" to exerciseId
                )
            )
            .addOnSuccessListener { deferred.complete(Result.Success(Unit)) }
            .addOnFailureListener { deferred.complete(Result.Failure(it)) }
        return withContext(Dispatchers.Main) {
            deferred.await()
        }

    }

    override suspend fun removeFromFav(userId: String, exerciseId: String): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()
        Firebase.firestore.collection("user_exercises").document("$userId-$exerciseId")
            .delete()
            .addOnSuccessListener { deferred.complete(Result.Success(Unit)) }
            .addOnFailureListener { Result.Failure(it) }
        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }
}