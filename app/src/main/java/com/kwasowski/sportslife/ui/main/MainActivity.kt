package com.kwasowski.sportslife.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityMainBinding
import com.kwasowski.sportslife.ui.main.appBarDays.DayFormat
import com.kwasowski.sportslife.ui.main.appBarDays.DaysAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val daysAdapter: DaysAdapter by inject()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            Timber.d("NavigationOnClick")
        }
        binding.topAppBar.setOnMenuItemClickListener {
            onMenuItemClickListener(it)
        }

        binding.daysList.setHasFixedSize(true)
        binding.daysList.adapter = daysAdapter

        val currentDate = Date()
        val daysList = mutableListOf<DayFormat>()

        val calendar = Calendar.getInstance()



        for (i in -10..10) {
            val day = currentDate.addDays(i)
            calendar.time = day
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.NARROW_FORMAT, Locale.getDefault())
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val dayFormat = DayFormat(dayOfWeek.uppercase(), dayOfMonth.toString())
            daysList.add(dayFormat)
        }

        daysAdapter.setDays(daysList)
        binding.daysList.scrollToPosition(daysAdapter.itemCount / 2)
    }

    private fun Date.addDays(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.select_date -> {
                Timber.d("Select date")
                true
            }

            else -> false
        }
        return false
    }
}