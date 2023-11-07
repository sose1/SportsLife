package com.kwasowski.sportslife.ui.activeTraining.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.settings.SettingsManager
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeriesInTraining
import com.kwasowski.sportslife.data.trainingPlan.SeriesInTraining
import com.kwasowski.sportslife.databinding.FragmentExerciseSeriesBinding
import com.kwasowski.sportslife.extensions.dp
import com.kwasowski.sportslife.extensions.serializable
import com.kwasowski.sportslife.ui.activeTraining.fragment.adapter.SeriesAdapter
import com.kwasowski.sportslife.ui.exercise.details.ExerciseDetailsActivity
import com.kwasowski.sportslife.utils.Constants
import com.kwasowski.sportslife.utils.UnitsTag
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseSeriesFragment : Fragment(), SeriesAdapter.OnSeriesCallback {
    private val settingsManager: SettingsManager by inject()
    private val viewModel: ExerciseSeriesViewModel by viewModel()
    private lateinit var binding: FragmentExerciseSeriesBinding
    private lateinit var seriesAdapter: SeriesAdapter
    private lateinit var exerciseSeries: ExerciseSeriesInTraining

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            exerciseSeries = arguments.serializable(Constants.EXERCISE_SERIES_BUNDLE_KEY)!!
        }
    }

    fun getExerciseSeries(): ExerciseSeriesInTraining = exerciseSeries

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exercise_series,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initSeriesAdapter()
        initUnitsInfo()
        initAddSeriesButton()
        initExerciseCard()

        return binding.root
    }

    private fun initSeriesAdapter() {
        seriesAdapter = SeriesAdapter(binding.root.context, this, exerciseSeries.series)
        binding.series.adapter = seriesAdapter
        resizeSeriesListView()
    }

    @SuppressLint("SetTextI18n")
    private fun initUnitsInfo() {
        val unitSettings = settingsManager.loadSettings().units
        when (exerciseSeries.units) {
            UnitsTag.MEASURE -> when (unitSettings) {
                Units.KG_M -> binding.valueUnit.text = getString(R.string.value) + "(m)"
                Units.LBS_MI -> binding.valueUnit.text = getString(R.string.value) + "(mi)"
            }

            UnitsTag.WEIGHT -> when (unitSettings) {
                Units.KG_M -> binding.valueUnit.text = getString(R.string.value) + "(kg)"
                Units.LBS_MI -> binding.valueUnit.text = getString(R.string.value) + "(lbs)"
            }

            UnitsTag.NONE -> Unit
        }
    }

    private fun initExerciseCard() {
        binding.exerciseName.text = exerciseSeries.exerciseName
        binding.exerciseCard.setOnClickListener {
            openExerciseDetails(exerciseSeries.originalId)
        }
        binding.exerciseMoreButton.setOnClickListener {
            openExerciseDetails(exerciseSeries.originalId)
        }
    }

    private fun initAddSeriesButton() {
        binding.addSeries.setOnClickListener {
            seriesAdapter.add(SeriesInTraining(0, 0, false))
            this.exerciseSeries.series = seriesAdapter.getAll()
            resizeSeriesListView()
        }
    }

    override fun onDeleteSeries() {
        exerciseSeries.series = seriesAdapter.getAll()
        resizeSeriesListView()
    }

    private fun resizeSeriesListView() {
        if (seriesAdapter.count >= 20)
            binding.series.layoutParams.height = exerciseSeries.series.size * 31.dp
        else
            binding.series.layoutParams.height = exerciseSeries.series.size * 30.dp
    }

    private fun openExerciseDetails(exerciseId: String) {
        val intent = Intent(requireContext(), ExerciseDetailsActivity::class.java)
        intent.putExtra(Constants.EXERCISE_ID_INTENT, exerciseId)
        intent.putExtra(Constants.TRAINING_PLAN_IS_DETAILS_VIEW, true)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(exerciseSeries: ExerciseSeriesInTraining): ExerciseSeriesFragment {
            val fragment = ExerciseSeriesFragment()
            val args = Bundle()
            args.putSerializable(Constants.EXERCISE_SERIES_BUNDLE_KEY, exerciseSeries)
            fragment.arguments = args
            return fragment
        }
    }
}