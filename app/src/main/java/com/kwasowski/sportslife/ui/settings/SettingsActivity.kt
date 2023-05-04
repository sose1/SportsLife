package com.kwasowski.sportslife.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.LocaleListCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.settings.Settings
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("SettingsActivity | onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        onViewStateChanged()

        binding.content.visibility = View.GONE
        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.selectLanguage.setOnClickListener { openLanguageMenu() }
        setOnUnitChangeListener()

        val x = AppCompatDelegate.getApplicationLocales()
        x.toLanguageTags()
        Timber.d("DUPA: ${x.toLanguageTags()}")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.d("SettingsActivity | onRestart")
        viewModel.isAfterInitSettings = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SettingsActivity | onDestory")
        viewModel.isAfterInitSettings = false
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    SettingsViewState.Default -> Unit
                    SettingsViewState.OnSelectLanguageClick -> openLanguageMenu()
                    SettingsViewState.OnGetSettingsError -> showSnackBarInfo(R.string.unable_to_download_settings)
                    is SettingsViewState.OnGetSettings -> onSettingsInit(it.settings)
                }
            }
        }
    }

    private fun onSettingsInit(settings: Settings) {
        when (settings.units) {
            Units.KG_M -> binding.unitsGroup.check(R.id.kg_m)
            Units.LBS_MI -> binding.unitsGroup.check(R.id.lbs_mi)
        }

        binding.notificationsTodayTrainings.isChecked = settings.notifyTodayTraining
        binding.notificationsTodaySummary.isChecked = settings.notifyDaySummary

        when (settings.language) {
            "en" -> onLanguageChanged("en", R.string.english)
            "pl" -> onLanguageChanged("pl", R.string.polish)
            else -> {
                val locales = AppCompatDelegate.getApplicationLocales()
                when (val tag = locales.toLanguageTags()) {
                    "en" -> onLanguageChanged(tag, R.string.english)
                    "pl" -> onLanguageChanged(tag, R.string.polish)
                    else -> onLanguageChanged("en", R.string.english)
                }
            }
        }
        viewModel.isAfterInitSettings = true

        binding.progress.visibility = View.GONE
        binding.content.visibility = View.VISIBLE

    }

    private fun setOnUnitChangeListener() {
        binding.unitsGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.kg_m -> {
                    viewModel.onUnitsChanged(Units.KG_M)
                }

                R.id.lbs_mi -> {
                    viewModel.onUnitsChanged(Units.LBS_MI)
                }

            }
        }
    }

    private fun openLanguageMenu() {
        val popup = PopupMenu(this, binding.selectLanguage)
        popup.menuInflater.inflate(R.menu.languages, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.polish -> {
                    onLanguageChanged("pl", R.string.polish)
                    true
                }

                R.id.english -> {
                    onLanguageChanged("en", R.string.english)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun onLanguageChanged(
        language: String,
        languageText: Int
    ) {
        binding.languageValue.text = getText(languageText)
        viewModel.onLanguageChanged(language)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }
}