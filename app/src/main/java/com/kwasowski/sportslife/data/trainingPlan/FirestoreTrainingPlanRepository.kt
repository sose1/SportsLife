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

    override suspend fun getTrainingPlanListByOwnerId(ownerId: String): Result<List<TrainingPlanDto>> =
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
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Failure(exception))
                }
        }
}