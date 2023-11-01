package com.kwasowski.sportslife.ui.activeTraining

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityActiveTrainingBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveTrainingActivity : AppCompatActivity() {
    private val viewModel: ActiveTrainingViewModel by viewModel()

    private lateinit var binding: ActivityActiveTrainingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_training)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        onViewStateChanged()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmExitDialog()
            }
        })
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    ActiveTrainingState.Default -> Unit
                }
            }
        }
    }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.do_you_want_to_complete_the_training)
            .setMessage(R.string.if_you_end_a_workout_before_completing_all_the_exercises_the_unapproved_series_will_not_be_saved)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                viewModel.onConfirmExit()
                finish()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}