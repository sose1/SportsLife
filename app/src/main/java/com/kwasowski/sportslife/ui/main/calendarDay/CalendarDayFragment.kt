package com.kwasowski.sportslife.ui.main.calendarDay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.FragmentCalendarDayBinding
import com.kwasowski.sportslife.utils.TimeLocationTag
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarDayFragment : Fragment() {
    private val viewModel: CalendarDayViewModel by viewModel()
    private lateinit var binding: FragmentCalendarDayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        binding.noDataBefore.visibility = View.GONE
        binding.noDataAfter.visibility = View.GONE

        if (dayID().isNullOrEmpty() && timeLocation().equals(TimeLocationTag.BEFORE)) {
            binding.noDataBefore.visibility = View.VISIBLE
        }
        if (dayID().isNullOrEmpty() && timeLocation() == TimeLocationTag.AFTER || timeLocation() == TimeLocationTag.ACTUAL
        ) {
            binding.noDataAfter.visibility = View.VISIBLE
        }

        // TODO: jesli jest id to pobierz dane przez viewmodel
        // TODO: pokazuj sekcje w zaleznosci od danych: jesli jest 0 SCHEDULED to sekcji nie pokazujemy - to samo z COMPLETED
        // TODO: Podsumowanie pokazujemy tylko, gdy mamy minimum 1 COMPLETED

        return binding.root

    }

    override fun onResume() {
        super.onResume()
    }

    private fun dayID(): String? = arguments?.getString(DAY_ID)
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