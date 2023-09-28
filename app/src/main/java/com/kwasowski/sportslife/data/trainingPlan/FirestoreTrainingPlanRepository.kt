package com.kwasowski.sportslife.data.trainingPlan

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

class FirestoreTrainingPlanRepository : TrainingPlanRepository {
    private val path = "training_plans"
    private val collection = Firebase.firestore.collection(path)

    override suspend fun saveTrainingPlan(id: String?, trainingPlan: TrainingPlan): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()

        val documentReference = if (id.isNullOrBlank()) {
            collection.document()
        } else {
            collection.document(id)
        }

        documentReference.set(trainingPlan, SetOptions.merge())
            .addOnSuccessListener {
                deferred.complete(Result.Success(Unit))
            }
            .addOnFailureListener {
                deferred.complete(Result.Failure(it))
            }

        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }

    override suspend fun getTrainingPlansByOwnerId(ownerId: String): Result<List<TrainingPlanDto>> =
        suspendCoroutine { continuation ->
            collection.whereEqualTo(TrainingPlan::ownerId.name, ownerId)
                .orderBy(TrainingPlan::updateDate.name, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val trainingPlanList = mutableListOf<TrainingPlanDto>()
                    querySnapshot.documents.forEach {
                        val trainingPlan = it.toObject<TrainingPlan>()
                        if (trainingPlan != null) {
                            trainingPlanList.add(
                                TrainingPlanDto(
                                    id = it.id,
                                    name = trainingPlan.name,
                                    description = trainingPlan.description,
                                    updateDate = trainingPlan.updateDate,
                                    ownerId = trainingPlan.ownerId,
                                    exercisesSeries = trainingPlan.exercisesSeries
                                )
                            )
                        }
                    }
                    continuation.resume(Result.Success(trainingPlanList))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Failure(exception))
                }
        }

    override suspend fun getSharedTrainingPlans(ownerId: String): Result<List<TrainingPlanDto>> =
        suspendCoroutine { continuation ->
            collection.whereEqualTo(TrainingPlan::shared.name, true)
                .whereNotEqualTo(TrainingPlan::ownerId.name, ownerId)
                .orderBy(TrainingPlan::ownerId.name)
                .orderBy(TrainingPlan::name.name, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val trainingPlanList = mutableListOf<TrainingPlanDto>()
                    querySnapshot.documents.forEach {
                        val trainingPlan = it.toObject<TrainingPlan>()
                        if (trainingPlan != null) {
                            trainingPlanList.add(
                                TrainingPlanDto(
                                    id = it.id,
                                    name = trainingPlan.name,
                                    description = trainingPlan.description,
                                    updateDate = trainingPlan.updateDate,
                                    ownerId = trainingPlan.ownerId,
                                    exercisesSeries = trainingPlan.exercisesSeries
                                )
                            )
                        }
                    }
                    continuation.resume(Result.Success(trainingPlanList))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Failure(exception))
                }
        }

    override suspend fun getTrainingPlanById(id: String): Result<TrainingPlanDto> =
        suspendCoroutine { continuation ->
            collection.document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject<TrainingPlan>()?.let {
                        val trainingPlanDto = TrainingPlanDto(
                            id = documentSnapshot.id,
                            name = it.name,
                            description = it.description,
                            updateDate = it.updateDate,
                            ownerId = it.ownerId,
                            shared = it.shared,
                            exercisesSeries = it.exercisesSeries
                        )
                        continuation.resume(Result.Success(trainingPlanDto))
                    } ?: Result.Failure(NullPointerException("Training plan not found"))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }

    override suspend fun deleteTrainingPlan(trainingPlanId: String): Result<Unit> {
        val deferred = CompletableDeferred<Result<Unit>>()
        collection.document(trainingPlanId).delete()
            .addOnSuccessListener { deferred.complete(Result.Success(Unit)) }
            .addOnFailureListener { Result.Failure(it) }
        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }
}