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
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.content.visibility = View.GONE
        binding.topAppBar.setNavigationOnClickListener { finish() }

        onViewStateChanged()
        setOnUnitChangeListener()
        binding.selectLanguage.setOnClickListener { openLanguageMenu() }
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    SettingsViewState.Default -> Unit
                    SettingsViewState.OnSelectLanguageClick -> openLanguageMenu()
                    SettingsViewState.OnGetSettingsError -> showSnackBarInfo(R.string.unable_to_download_settings)
                }
            }
        }
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
        languageText: Int) {
        binding.languageValue.text = getText(languageText)
        viewModel.onLanguageChanged(language)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }
}