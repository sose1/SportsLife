package com.kwasowski.sportslife.ui.main.calendarDay

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.FragmentCalendarDayBinding
import com.kwasowski.sportslife.ui.trainingPlans.list.TrainingPlansActivity
import com.kwasowski.sportslife.utils.ActivityOpenMode
import com.kwasowski.sportslife.utils.Constants
import com.kwasowski.sportslife.utils.TimeLocationTag
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CalendarDayFragment : Fragment() {
    private val viewModel: CalendarDayViewModel by viewModel()
    private lateinit var binding: FragmentCalendarDayBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_calendar_day,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        onViewStateChanged()

        binding.noDataBefore.visibility = View.GONE
        binding.noDataAfter.visibility = View.GONE

        if (dayID().isNotEmpty()) {
            viewModel.getDay(dayID())
        } else {
            when (timeLocation()) {
                TimeLocationTag.BEFORE -> showNoDataBefore()
                TimeLocationTag.AFTER -> showNoDataAfter()
                TimeLocationTag.ACTUAL -> showNoDataAfter()
            }
        }

        // TODO: Sekcja Podsumowania


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                Timber.d("STATE: $it")
                when (it) {
                    CalendarDayState.Default -> Unit
                    is CalendarDayState.OnSuccessGetDay -> onSuccessGetDay(
                        it.scheduledTrainings,
                        it.completedTrainings
                    )
                }
            }
        }
    }

    private fun onSuccessGetDay(
        scheduledTrainings: List<Training>,
        completedTrainings: List<Training>,
    ) {
        binding.progress.visibility = View.GONE

        if (scheduledTrainings.isEmpty()) {
            disableScheduledTrainings()
        } else {
            showScheduledTrainings(scheduledTrainings)
        }

        if (completedTrainings.isEmpty()) {
            disableCompletedTrainings()
        } else {
            showCompletedTrainings(completedTrainings)
        }
    }

    private fun showScheduledTrainings(scheduledTrainings: List<Training>) {
        val scheduledTrainingsAdapter = ScheduledTrainingsAdapter()
        binding.scheduledTrainingsList.setHasFixedSize(true)
        binding.scheduledTrainingsList.adapter = scheduledTrainingsAdapter
        scheduledTrainingsAdapter.updateList(scheduledTrainings)
        binding.scheduledTrainings.visibility = View.VISIBLE

        binding.scheduleTrainingButton.setOnClickListener { openTrainingPlanListActivity() }
    }

    private fun showCompletedTrainings(completedTrainings: List<Training>) {
        val completedTrainingsAdapter = CompletedTrainingsAdapter()
        binding.completedTrainingsList.setHasFixedSize(true)
        binding.completedTrainingsList.adapter = completedTrainingsAdapter
        completedTrainingsAdapter.updateList(completedTrainings)
        binding.completedTrainings.visibility = View.VISIBLE
    }

    private fun disableScheduledTrainings() {
        binding.scheduledTrainings.visibility = View.GONE
    }

    private fun disableCompletedTrainings() {
        binding.completedTrainings.visibility = View.GONE
    }

    private fun showNoDataBefore() {
        binding.progress.visibility = View.GONE
        binding.noDataBefore.visibility = View.VISIBLE
    }

    private fun showNoDataAfter() {
        binding.progress.visibility = View.GONE
        binding.noDataAfter.visibility = View.VISIBLE

        binding.searchTrainingPlanButton.setOnClickListener { openTrainingPlanListActivity() }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Timber.d("ResultCode: ${result.resultCode}")
        }

    private fun openTrainingPlanListActivity() {
        viewModel.setStateToDefault()

        val intent = Intent(requireContext(), TrainingPlansActivity::class.java)
        intent.putExtra(
            Constants.OPEN_MODE,
            ActivityOpenMode.ADD_TRAINING_PLAN_TO_CALENDAR_DAY as Parcelable
        )
        activityResultLauncher.launch(intent)

        /**
         * FLOW z MainActivity:
         */
        // TODO: Dodać w TrainingPlansActivity zamykanie activity takie jak przy ExercisesListActivity
        // TODO: Dodać obsługe przycisków planowania w ItemTrainingPLan (te same flow)
        // TODO: Po powrocie z TrainingPlansActivity wykonać zapytanie zapisujące w Firestore
       // todo https://chat.openai.com/share/e3b44c8e-7d4d-48e7-a9af-d161e891ae91
        /**
         * FLOW z TrainingPlansActivity:
         */
        // TODO: Zaplanowanie ćwiczenia prosto z TrainingPlansActivity:
        // TODO: 1. jesli open mode nie pochodzi z tego fragmentu to otwórz DatePicker i pobierz dane calendar.
        // TODO: 2. Po potwierdzeniu daty w DatePicker wykonać zapytanie zapisujące do danego dnia zmapować po dacie

    }

    private fun dayID(): String = arguments?.getString(DAY_ID) ?: ""
    private fun timeLocation(): String? = arguments?.getString(TIME_LOCATION)

    companion object {
        const val DAY_ID = "DAY_ID"
        const val TIME_LOCATION = "TIME_LOCATION"

        @JvmStatic
        fun newInstance(dayId: String, timeLocation: String): CalendarDayFragment {
            val fragment = CalendarDayFragment()
            val args = Bundle()
            args.putString(DAY_ID, dayId)
            args.putString(TIME_LOCATION, timeLocation)
            fragment.arguments = args
            return fragment
        }
    }
}