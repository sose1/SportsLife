package com.kwasowski.sportslife.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
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
import com.kwasowski.sportslife.ui.login.LoginActivity
import com.kwasowski.sportslife.ui.main.appBarDays.Day
import com.kwasowski.sportslife.ui.main.appBarDays.DaysAdapter
import com.kwasowski.sportslife.ui.main.calendarDay.CalendarDayFragment
import com.kwasowski.sportslife.ui.profile.ProfileActivity
import com.kwasowski.sportslife.ui.settings.SettingsActivity
import com.kwasowski.sportslife.ui.trainingPlans.list.TrainingPlansActivity
import com.kwasowski.sportslife.utils.Constants
import com.kwasowski.sportslife.utils.LanguageTag
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(){
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager

    private var dayIndex = 0
    private var dayId: String = ""
    private var timeLocation = ""
    private var number = 0
    private var month = 0
    private var year = 0
    private var todayIndex = 0

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
        window.statusBarColor = Color.TRANSPARENT
        onViewStateChanged()
        initDaysAdapter()

        binding.navigationView.setCheckedItem(R.id.calendar_item)
        binding.navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }

        binding.topAppBar.setNavigationOnClickListener { binding.drawerLayout.open() }
        binding.topAppBar.setOnMenuItemClickListener { onMenuItemClickListener(it) }
    }

    override fun onResume() {
        super.onResume()
        binding.navigationView.setCheckedItem(R.id.calendar_item)
        if (dayIndex != 0 && todayIndex != 0) {
            viewModel.refreshCalendar()
        } else {
            clearFragments()
            viewModel.initializeDays(true)
        }
    }

    fun refreshCalendar() {
        viewModel.refreshCalendar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        clearFragments()
        viewModel.refreshCalendar()
        onDayItemClick(dayIndex, dayId, timeLocation, number, month, year)
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                Timber.d("MainActivity STATE: $it")
                when (it) {
                    MainViewState.Default -> Unit
                    is MainViewState.OnInitDays -> onInitDays(it.days, it.todayIndex)
                    is MainViewState.OnDaysListUpdate -> onDaysListUpdate(it.days)
                    is MainViewState.OnDayItemClick -> onDayItemClick(
                        it.indexOf,
                        it.dayId,
                        it.timeLocation,
                        it.number,
                        it.month,
                        it.year
                    )

                    is MainViewState.OnDataPickerOpen -> onDatePickerOpen(it.constraints)
                    is MainViewState.OnIndexOutOfBoundsException -> showSnackBarInfo(R.string.you_cannot_select_this_date_please_try_another_one)
                    is MainViewState.OnTitleChange -> onTitleChange(it.month, it.year)
                    MainViewState.OnLogout -> openActivity(LoginActivity::class.java)
                    is MainViewState.OnGetSettings -> onGetSettings(it.language)
                    MainViewState.OnCalendarError -> showToast(R.string.cannot_download_calendar_data)
                    is MainViewState.ClickSelectedDay -> {
                        onDaysListUpdate(viewModel.daysList)
                        onDayItemClick(dayIndex, it.id, timeLocation, number, month, year)
                    }
                }
            }
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calendarIsReady.collect { isReady ->
                    if (isReady) {
                        this@MainActivity.todayIndex = todayIndex
                        viewModel.onDayItemClick(days[todayIndex])
                    }
                }
            }
        }
    }

    private fun onDaysListUpdate(days: List<Day>) {
        daysAdapter.updateList(days)
    }

    private fun onDayItemClick(
        indexOf: Int,
        dayId: String,
        timeLocation: String,
        number: Int,
        month: Int,
        year: Int,
    ) {
        this.dayIndex = indexOf
        this.dayId = dayId
        this.timeLocation = timeLocation
        this.number = number
        this.month = month
        this.year = year
        binding.daysList.scrollToPosition(indexOf)

        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach {
            transaction.remove(it)
        }

        // Tworzenie nowego fragmentu
        val fragment = CalendarDayFragment.newInstance(
            dayId = dayId,
            timeLocation = timeLocation,
            number = number,
            month = month,
            year = year
        )

        // Rozpocznij transakcję fragmentu
        transaction.add(R.id.day_fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        viewModel.setDefaultState()
    }

    private fun clearFragments() {
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach {
            transaction.remove(it)
        }
        transaction.commit()
    }

    private fun onNavigationItemSelected(it: MenuItem): Boolean {
        return when (it.itemId) {
            R.id.calendar_item -> {
                true
            }

            R.id.training_plans_item -> {
                openActivity(TrainingPlansActivity::class.java)
                true
            }

            R.id.exercises_list -> {
                openActivity(ExercisesListActivity::class.java)
                true
            }

            R.id.profile_item -> {
                openActivity(ProfileActivity::class.java)
                true
            }

            R.id.settings_item -> {
                openActivity(SettingsActivity::class.java)
                true
            }

            R.id.logout_item -> {
                viewModel.onLogoutClick()
                true
            }

            else -> false
        }
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

    private fun onDatePickerOpen(constraints: CalendarConstraints) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setTitleText(R.string.select_date)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener { viewModel.onSelectedDateInDatePicker(it) }
        datePicker.addOnCancelListener { viewModel.onDatePickerClose() }
        datePicker.addOnNegativeButtonClickListener { viewModel.onDatePickerClose() }
        datePicker.show(supportFragmentManager, Constants.DATE_PICKER_TAG)
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }

    private fun openActivity(activity: Class<out AppCompatActivity>) {
        Timber.d("openActivity() | $activity")
        startActivity(Intent(this, activity))
    }

    private fun onGetSettings(language: String) {
        when (language) {
            LanguageTag.EN -> onLanguageChanged(LanguageTag.EN)
            LanguageTag.PL -> onLanguageChanged(LanguageTag.PL)
            else -> {
                val locales = AppCompatDelegate.getApplicationLocales()
                when (val tag = locales.toLanguageTags()) {
                    LanguageTag.EN -> onLanguageChanged(tag)
                    LanguageTag.PL -> onLanguageChanged(tag)
                    else -> onLanguageChanged(LanguageTag.EN)
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