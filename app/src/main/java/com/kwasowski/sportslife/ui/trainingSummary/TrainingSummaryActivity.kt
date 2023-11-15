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

    private lateinit var exerciseSeriesSummaryAdapter: ExerciseSeriesSummaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_summary)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        onViewStateChanged()
        viewModel.trainingLiveData.value = getTrainingFromIntent() ?: Training()

        if (viewModel.trainingLiveData.value?.note?.isNotEmpty() == true) {
            viewModel.note.value = viewModel.trainingLiveData.value?.note
        }

        exerciseSeriesSummaryAdapter = ExerciseSeriesSummaryAdapter()
        exerciseSeriesSummaryAdapter.updateList(
            viewModel.trainingLiveData.value?.trainingPlan?.exercisesSeries ?: emptyList()
        )
        binding.exerciseSeriesSummaryList.setHasFixedSize(true)
        binding.exerciseSeriesSummaryList.adapter = exerciseSeriesSummaryAdapter

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getTrainingFromIntent()?.let { viewModel.saveTraining(getDayIdFromIntent(), it) }
            }
        })
        binding.topAppBar.setNavigationOnClickListener {
            getTrainingFromIntent()?.let { viewModel.saveTraining(getDayIdFromIntent(), it) }
        }
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                Timber.d("TrainingSummaryActivity STATE: $it")
                when (it) {
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