package com.kwasowski.sportslife.ui.trainingSummary

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.ActivityTrainingSummaryBinding
import com.kwasowski.sportslife.extensions.serializable
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TrainingSummaryActivity : AppCompatActivity() {
    private val viewModel: TrainingSummaryViewModel by viewModel()

    private lateinit var binding: ActivityTrainingSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_summary)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        onViewStateChanged()
        viewModel.trainingLiveData.value = getTrainingFromIntent() ?: Training()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getTrainingFromIntent()?.let { viewModel.saveTraining(getDayIdFromIntent(), it) }
            }
        })
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect{
                Timber.d("TrainingSummaryActivity STATE: $it")
                when(it){
                    TrainingSummaryState.Default -> Unit
                    TrainingSummaryState.OnSuccessSaveTraining -> finish()
                }
            }
        }
    }

    private fun getDayIdFromIntent() =
        intent?.getStringExtra(Constants.DAY_ID_INTENT) ?: ""

    private fun getTrainingFromIntent(): Training? = intent.serializable(Constants.TRAINING_INTENT)
}