package com.kwasowski.sportslife.ui.main.calendarDay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.FragmentCalendarDayBinding
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