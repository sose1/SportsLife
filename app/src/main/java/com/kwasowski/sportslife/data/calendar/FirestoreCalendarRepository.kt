package com.kwasowski.sportslife.data.calendar

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirestoreCalendarRepository : CalendarRepository {
    private val path = "calendars"
    private val collection = Firebase.firestore.collection(path)

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
}
