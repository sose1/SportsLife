package com.kwasowski.sportslife.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
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
import com.kwasowski.sportslife.databinding.ActivityMainBinding
import com.kwasowski.sportslife.ui.exercise.exerciseList.activity.ExercisesListActivity
import com.kwasowski.sportslife.ui.exercise.favorite.FavExercisesActivity
import com.kwasowski.sportslife.ui.login.LoginActivity
import com.kwasowski.sportslife.ui.main.appBarDays.Day
import com.kwasowski.sportslife.ui.main.appBarDays.DaysAdapter
import com.kwasowski.sportslife.ui.profile.ProfileActivity
import com.kwasowski.sportslife.ui.settings.SettingsActivity
import com.kwasowski.sportslife.ui.trainingLog.TrainingLogActivity
import com.kwasowski.sportslife.ui.trainingPlans.TrainingPlansActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager

    private val daysAdapter = DaysAdapter {
        viewModel.onDayItemClick(it)
    }

    private val onRecyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                binding.topAppBar.setTitle(R.string.app_name)

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            when {
                dx < 0 -> viewModel.onScrollDays(layoutManager.findFirstVisibleItemPosition())
                dx > 0 -> viewModel.onScrollDays(layoutManager.findLastVisibleItemPosition())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.initializeDays()

        window.statusBarColor = Color.TRANSPARENT

        binding.navigationView.setCheckedItem(R.id.calendar_item)
        binding.navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }

        binding.topAppBar.setNavigationOnClickListener { binding.drawerLayout.open() }
        binding.topAppBar.setOnMenuItemClickListener { onMenuItemClickListener(it) }

        onViewStateChanged()
        initDaysAdapter()
    }

    override fun onResume() {
        super.onResume()
        binding.navigationView.setCheckedItem(R.id.calendar_item)

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
                    MainViewState.OnLogout -> openLoginActivity()
                    is MainViewState.OnGetSettings -> onGetSettings(it.language)
                }
            }
        }
    }

    private fun onNavigationItemSelected(it: MenuItem): Boolean {
        return when (it.itemId) {
            R.id.calendar_item -> {
                true
            }

            R.id.training_log_item -> {
                openTrainingLogActivity()
                true
            }

            R.id.training_plans_item -> {
                openTrainingPlansActivity()
                true
            }

            R.id.exercises_list -> {
                openExercisesListActivity()
                true
            }

            R.id.favorite_exercises_item -> {
                openFavoriteExercisesActivity()
                true
            }

            R.id.profile_item -> {
                openProfileActivity()
                true
            }

            R.id.settings_item -> {
                openSettingsActivity()
                true
            }

            R.id.logout_item -> {
                viewModel.onLogoutClick()
                true
            }

            else -> false
        }
    }

    private fun initDaysAdapter() {
        binding.daysList.setHasFixedSize(true)
        binding.daysList.adapter = daysAdapter
        layoutManager = binding.daysList.layoutManager as LinearLayoutManager
        binding.daysList.addOnScrollListener(onRecyclerViewScrollListener)
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

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
        R.id.select_date -> {
            viewModel.onDataPickerOpen()
            true
        }

        else -> false
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

    private fun openTrainingLogActivity() {
        Timber.d("openTrainingLogActivity()")
        startActivity(Intent(this, TrainingLogActivity::class.java))
    }

    private fun openTrainingPlansActivity() {
        Timber.d("openTrainingPlansActivity()")
        startActivity(Intent(this, TrainingPlansActivity::class.java))
    }

    private fun openExercisesListActivity() {
        Timber.d("openExercisesListActivity()")
        startActivity(Intent(this, ExercisesListActivity::class.java))
    }

    private fun openFavoriteExercisesActivity() {
        Timber.d("openFavoriteExercisesActivity()")
        startActivity(Intent(this, FavExercisesActivity::class.java))
    }

    private fun openProfileActivity() {
        Timber.d("openProfileActivity()")
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun openSettingsActivity() {
        Timber.d("openSettingsActivity()")
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openLoginActivity() {
        Timber.d("openLoginActivity()")
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun onGetSettings(language: String) {
        when (language) {
            "en" -> onLanguageChanged("en")
            "pl" -> onLanguageChanged("pl")
            else -> {
                val locales = AppCompatDelegate.getApplicationLocales()
                when (val tag = locales.toLanguageTags()) {
                    "en" -> onLanguageChanged(tag)
                    "pl" -> onLanguageChanged(tag)
                    else -> onLanguageChanged("en")
                }
            }
        }
    }

    private fun onLanguageChanged(
        language: String,
    ) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}