package com.kwasowski.sportslife.ui.activeTraining.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.FragmentExerciseSeriesBinding
import com.kwasowski.sportslife.extensions.dp
import com.kwasowski.sportslife.ui.activeTraining.fragment.adapter.SeriesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ExerciseSeriesFragment : Fragment(), SeriesAdapter.OnSeriesCallback {
    private val viewModel: ExerciseSeriesViewModel by viewModel()
    private lateinit var binding: FragmentExerciseSeriesBinding
    private lateinit var seriesAdapter: SeriesAdapter


    private var exerciseSeries =
        ExerciseSeries(
            "id", "name", "units", listOf<Series>()
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exercise_series,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        seriesAdapter = SeriesAdapter(binding.root.context, this)
        seriesAdapter.addAll(exerciseSeries.series.toMutableList())
        binding.series.adapter = seriesAdapter

        binding.addSeries.setOnClickListener {
            Timber.d("XD")
            seriesAdapter.add(Series())
            this.exerciseSeries.series = seriesAdapter.getAll()
            resizeSeriesListView()
        }

        return binding.root
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
}