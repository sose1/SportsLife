package com.kwasowski.sportslife.ui.main.calendarDay

import ParcelableMutableList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwasowski.sportslife.data.Result
import com.kwasowski.sportslife.data.calendar.Day
import com.kwasowski.sportslife.data.calendar.DayDto
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.data.calendar.TrainingState
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeriesInTraining
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.data.trainingPlan.SeriesInTraining
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanInTraining
import com.kwasowski.sportslife.domain.calendar.GetSingleDayUseCase
import com.kwasowski.sportslife.domain.calendar.SaveSingleDayUseCase
import com.kwasowski.sportslife.domain.trainingPlan.GetTrainingPlanUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CalendarDayViewModel(
    private val getSingleDayUseCase: GetSingleDayUseCase,
    private val saveSingleDayUseCase: SaveSingleDayUseCase,
    private val getTrainingPlanUseCase: GetTrainingPlanUseCase,
) :
    ViewModel() {


    private val mutableState = MutableStateFlow<CalendarDayState>(CalendarDayState.Default)
    val uiState: StateFlow<CalendarDayState> = mutableState.asStateFlow()

    private lateinit var day: Day

    fun getDay(dayID: String?) {
        if (dayID.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            getSingleDayUseCase.execute(dayID).let {
                when (it) {
                    is Result.Failure -> if (it.exception is NullPointerException) mutableState.value =
                        CalendarDayState.OnEmptyDay

                    is Result.Success -> onSuccessGetDay(it.data)
                }
            }
        }
    }

    private fun onSuccessGetDay(data: DayDto) {
        val scheduledTrainings = data.trainingList.filter { it.state == TrainingState.SCHEDULED }
        val completedTrainings = data.trainingList.filter { it.state == TrainingState.COMPLETED }

        day = Day(
            data.number,
            data.month,
            data.year,
            data.trainingList,
        )

        mutableState.value =
            CalendarDayState.OnSuccessGetDay(scheduledTrainings, completedTrainings)
    }

    fun setStateToDefault() {
        mutableState.value = CalendarDayState.Default
    }

    fun saveDay(
        dayID: String?,
        number: Int,
        month: Int,
        year: Int,
        newTrainingPlanData: ParcelableMutableList<*>,
    ) {
        viewModelScope.launch {
            val trainingListDeferreds = newTrainingPlanData.map { data ->
                async {
                    data["trainingPlanId"]?.let { id ->
                        when (val result = getTrainingPlanUseCase.execute(id)) {
                            is Result.Success -> Training(
                                name = result.data.name,
                                state = TrainingState.SCHEDULED,
                                trainingPlan = trainingPlanConvertToTrainingPlanInTraining(result.data),
                            )

                            is Result.Failure -> {
                                Timber.d("${result.exception}")
                                null
                            }
                        }
                    }
                }
            }

            val trainingList = trainingListDeferreds.awaitAll().filterNotNull()

            if (dayID.isNullOrEmpty()) {
                day = Day(number, month, year, trainingList)
            } else {
                day.trainingList = (day.trainingList) + trainingList
            }

            when (val result = saveSingleDayUseCase.execute(dayID, day)) {
                is Result.Success -> {
                    Timber.d(result.data)
                    mutableState.value = CalendarDayState.OnSuccessSave
                }

                is Result.Failure -> {
                    Timber.d("${result.exception}")
                }
            }
        }
    }

    fun deleteTrainingPlan(
        dayID: String?,
        trainingPlan: Training,
    ) {
        day.trainingList = (day.trainingList) - listOf(trainingPlan).toSet()
        viewModelScope.launch {
            when (val result = saveSingleDayUseCase.execute(dayID, day)) {
                is Result.Failure -> Timber.d("${result.exception}")
                is Result.Success -> {
                    mutableState.value = CalendarDayState.OnSuccessSave
                }
            }
        }
    }

    private fun trainingPlanConvertToTrainingPlanInTraining(
        trainingPlan: TrainingPlanDto,
    ): TrainingPlanInTraining {
        return TrainingPlanInTraining(
            id = trainingPlan.id,
            name = trainingPlan.name,
            description = trainingPlan.description,
            ownerId = trainingPlan.ownerId,
            shared = trainingPlan.shared,
            updateDate = trainingPlan.updateDate,
            exercisesSeries = exerciseSeriesConvertToExerciseSeriesInTraining(trainingPlan.exercisesSeries)
        )
    }

    private fun exerciseSeriesConvertToExerciseSeriesInTraining(
        exerciseSeries: List<ExerciseSeries>,
    ): List<ExerciseSeriesInTraining> {
        return exerciseSeries.map {
            ExerciseSeriesInTraining(
                originalId = it.originalId,
                exerciseName = it.exerciseName,
                units = it.units,
                series = seriesConvertToSeriesInTraining(it.series)
            )
        }
    }

    private fun seriesConvertToSeriesInTraining(
        series: List<Series>,
    ): List<SeriesInTraining> {
        return series.map {
            SeriesInTraining(
                value = it.value,
                repeats = it.repeats,
                completed = false
            )
        }
    }
}