package com.kwasowski.sportslife.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.model.Day
import com.kwasowski.sportslife.databinding.ActivityMainBinding
import com.kwasowski.sportslife.ui.main.appBarDays.DaysAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private val daysAdapter = DaysAdapter {
        viewModel.onDayItemClick(it)
    }

    private lateinit var binding: ActivityMainBinding

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
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    MainViewState.Default -> Unit
                    is MainViewState.OnInitDays -> onInitDays(it.days)
                    is MainViewState.OnDayItemClick -> onDayItemClick(it.days)
                }
            }
        }
    }

    private fun onDayItemClick(days: List<Day>) {
        daysAdapter.updateList(days)
    }

    private fun onInitDays(days: List<Day>) {
        daysAdapter.updateList(days)
        binding.daysList.scrollToPosition(daysAdapter.itemCount / 2)
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