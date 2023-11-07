package com.kwasowski.sportslife.domain.calendar

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.CalendarRepository
import com.kwasowski.sportslife.data.calendar.Training

class SaveTrainingUseCase(private val calendarRepository: CalendarRepository) {
    suspend fun execute(dayId: String, trainingId: String, training: Training): Result<String> {
        val uid = Firebase.auth.currentUser?.uid
            ?: return Result.Failure(NullPointerException("UID is null"))
        return calendarRepository.saveTraining(dayId, trainingId, uid, training)
    }
}