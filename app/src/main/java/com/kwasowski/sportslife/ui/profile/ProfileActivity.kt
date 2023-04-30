package com.kwasowski.sportslife.ui.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.profile.Gender
import com.kwasowski.sportslife.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModel()

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.content.visibility = View.GONE
        binding.topAppBar.setNavigationOnClickListener { finish() }

        binding.heightSlider.addOnSliderTouchListener(onHeightSliderTouchListener)
        binding.weightSlider.addOnSliderTouchListener(onWeightSliderTouchListener)

        setOnGenderChangeListener()
        onViewStateChanged()
        lifecycleScope.launch {
            viewModel.getProfile()
        }
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    ProfileViewState.Default -> Unit
                    is ProfileViewState.OnBMIChanged -> binding.bmiValue.text = it.BMI.toString()
                    is ProfileViewState.OnGetProfile -> updateProfileUI(it.gender, it.height, it.weight, it.bmi)
                }
            }
        }
    }

    private fun setOnGenderChangeListener() {
        binding.genderGroup.setOnCheckedChangeListener { group, checkedId ->
            lifecycleScope.launch {
                when (checkedId) {
                    R.id.woman -> {
                        viewModel.onGenderChanged(Gender.WOMAN)
                    }

                    R.id.man -> {
                        viewModel.onGenderChanged(Gender.MAN)
                    }
                }
            }
        }
    }

    private val onHeightSliderTouchListener = object : Slider.OnSliderTouchListener {
        override fun onStartTrackingTouch(slider: Slider) {
        }

        override fun onStopTrackingTouch(slider: Slider) {
            Timber.d("onStopTrackingTouch: ${slider.value}")
            binding.heightValue.text = slider.value.roundToInt().toString()
            lifecycleScope.launch {
                viewModel.updateProfile(newHeight = slider.value.roundToInt())
            }
        }
    }

    private val onWeightSliderTouchListener = object : Slider.OnSliderTouchListener {
        override fun onStartTrackingTouch(slider: Slider) {
        }

        override fun onStopTrackingTouch(slider: Slider) {
            Timber.d("onStopTrackingTouch: ${slider.value}")
            binding.weightValue.text = slider.value.roundToInt().toString()
            lifecycleScope.launch {
                viewModel.updateProfile(newWeight = slider.value.roundToInt())
            }
        }
    }

    private fun updateProfileUI(gender: Gender, height: Int, weight: Int, bmi: Double) {
        when (gender) {
            Gender.UNKNOWN -> Unit
            Gender.WOMAN -> binding.genderGroup.check(R.id.woman)
            Gender.MAN -> binding.genderGroup.check(R.id.man)
        }
        binding.heightSlider.value = height.toFloat()
        binding.heightValue.text = height.toString()

        binding.weightSlider.value = weight.toFloat()
        binding.weightValue.text = weight.toString()

        binding.bmiValue.text = bmi.toString()
        binding.progress.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
    }
}