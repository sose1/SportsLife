package com.kwasowski.sportslife.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.model.Day
import com.kwasowski.sportslife.databinding.ActivityMainBinding
import com.kwasowski.sportslife.ui.main.appBarDays.DaysAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    private val daysAdapter = DaysAdapter {
        viewModel.onDayItemClick(it)
    }

    private val onRecyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val layoutManager = binding.daysList.layoutManager as LinearLayoutManager
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                viewModel.onScrollDays(layoutManager.findFirstVisibleItemPosition())
            } else {
                binding.topAppBar.setTitle(R.string.app_name)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        onViewStateChanged()
        viewModel.initializeDays()

        binding.topAppBar.setNavigationOnClickListener { Timber.d("NavigationOnClick") }
        binding.topAppBar.setOnMenuItemClickListener { onMenuItemClickListener(it) }

        binding.daysList.setHasFixedSize(true)
        binding.daysList.adapter = daysAdapter
        binding.daysList.addOnScrollListener(onRecyclerViewScrollListener)
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    MainViewState.Default -> Unit
                    is MainViewState.OnInitDays -> onInitDays(it.days, it.todayIndex)
                    is MainViewState.OnDaysListUpdate -> onDaysListUpdate(it.days)
                    is MainViewState.OnDayItemClick -> onDayItemClick(it.day, it.indexOf)
                    is MainViewState.OnDataPickerOpen -> onDataPickerOpen(it.constraints)
                    is MainViewState.OnIndexOutOfBoundsException -> showSnackBarInfo(R.string.you_cannot_select_this_date_please_try_another_one)
                    is MainViewState.OnTitleChange -> onTitleChange(it.month, it.year)
                }
            }
        }
    }

    private fun onInitDays(days: List<Day>, todayIndex: Int) {
        daysAdapter.updateList(days)
        onDayItemClick(days[todayIndex], todayIndex)
    }

    private fun onDaysListUpdate(days: List<Day>) {
        daysAdapter.updateList(days)
    }

    private fun onDayItemClick(day: Day, indexOf: Int) {
        binding.text.text = "$day"
        binding.daysList.scrollToPosition(indexOf)
    }

    private fun onTitleChange(month: CharSequence, year: Int) {
        binding.topAppBar.title = "${month.toString().replaceFirstChar { it.uppercase() }} $year"
    }

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.select_date -> {
                viewModel.onDataPickerOpen()
                true
            }

            else -> false
        }
        return false
    }

    private fun onDataPickerOpen(constraints: CalendarConstraints) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setTitleText(R.string.select_date)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.onSelectedDateInDatePicker(it)
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }
}