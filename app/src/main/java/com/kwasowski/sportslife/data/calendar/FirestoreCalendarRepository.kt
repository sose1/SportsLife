package com.kwasowski.sportslife.data.calendar

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

class FirestoreCalendarRepository : CalendarRepository {
    private val path = "calendars"

    override suspend fun getCalendarByOwnerId(ownerId: String): Result<Calendar> =
        suspendCoroutine { continuation ->
            Firebase.firestore.collection("$path/$ownerId/days").get()
                .addOnSuccessListener { documentSnapshot ->
                    val calendar = Calendar()
                    val days = mutableListOf<DayDto>()

                    documentSnapshot.documents.forEach { dayDocumentSnapshot ->
                        dayDocumentSnapshot.toObject<Day>()?.let {
                            days.add(
                                DayDto(
                                    id = dayDocumentSnapshot.id,
                                    number = it.number,
                                    month = it.month,
                                    year = it.year,
                                    trainingList = it.trainingList
                                )
                            )
                        }
                    }
                    calendar.days = days
                    continuation.resume(Result.Success(calendar))
                }.addOnFailureListener { continuation.resume(Result.Failure(it)) }
        }

    override suspend fun getSingleDay(dayId: String, ownerId: String): Result<DayDto> =
        suspendCoroutine { continuation ->
            Firebase.firestore.collection("$path/$ownerId/days").document(dayId).get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject<Day>()?.let {
                        val dayDto = DayDto(
                            id = documentSnapshot.id,
                            number = it.number,
                            month = it.month,
                            year = it.year,
                            trainingList = it.trainingList
                        )
                        continuation.resume(Result.Success(dayDto))
                    } ?: continuation.resume(Result.Failure(NullPointerException("Day not found")))
                }.addOnFailureListener { continuation.resume(Result.Failure(it)) }
        }

    override suspend fun saveSingleDay(
        dayId: String?,
        ownerId: String,
        day: Day,
    ): Result<String> {
        val deferred = CompletableDeferred<Result<String>>()
        val collection = Firebase.firestore.collection("$path/$ownerId/days")
        val documentReference = if (dayId.isNullOrBlank()) {
            collection.document()
        } else {
            collection.document(dayId)
        }

        documentReference.set(day, SetOptions.merge())
            .addOnSuccessListener { _ ->
                deferred.complete(Result.Success(documentReference.id))
            }
            .addOnFailureListener { exception ->
                deferred.complete(Result.Failure(exception))
            }

        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }

    override suspend fun getTraining(
        dayId: String,
        trainingId: String,
        uid: String,
    ): Result<Training> =
        suspendCoroutine { continuation ->
            Firebase.firestore.collection("$path/$uid/days").document(dayId).get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject<Day>()?.let {
                        val dayDto = DayDto(
                            id = documentSnapshot.id,
                            number = it.number,
                            month = it.month,
                            year = it.year,
                            trainingList = it.trainingList
                        )
                        dayDto.trainingList.find { training -> training.id == trainingId }?.let {
                            continuation.resume(Result.Success(it))
                        }
                            ?: continuation.resume(Result.Failure(NullPointerException("Training not found")))
                    } ?: continuation.resume(Result.Failure(NullPointerException("Day not found")))
                }.addOnFailureListener { continuation.resume(Result.Failure(it)) }
        }

    override suspend fun saveTraining(
        dayId: String,
        trainingId: String,
        ownerId: String,
        training: Training,
    ): Result<String> {
        val deferred = CompletableDeferred<Result<String>>()
        val collection = Firebase.firestore.collection("$path/$ownerId/days")
        val documentReference = if (dayId.isNullOrBlank()) {
            collection.document()
        } else {
            collection.document(dayId)
        }
        var dayDto = DayDto()
        when (val day = getSingleDay(dayId, ownerId)) {
            is Result.Failure -> TODO()
            is Result.Success -> dayDto = day.data
        }

        val updatedTrainingList = dayDto.trainingList.toMutableList()

        updatedTrainingList.find { trainingId == training.id }?.let {
            updatedTrainingList.remove(it)
        }
        updatedTrainingList.add(training)

        dayDto.trainingList = updatedTrainingList

        documentReference.set(
            Day(
                number = dayDto.number,
                month = dayDto.month,
                year = dayDto.year,
                trainingList = dayDto.trainingList
            ), SetOptions.merge()
        )
            .addOnSuccessListener { _ ->
                deferred.complete(Result.Success(documentReference.id))
            }
            .addOnFailureListener { exception ->
                deferred.complete(Result.Failure(exception))
            }

        return withContext(Dispatchers.Main) {
            deferred.await()
        }
    }
}