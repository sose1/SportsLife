package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemExerciseSeriesBinding
import com.kwasowski.sportslife.extensions.dp
import timber.log.Timber
import kotlin.random.Random

class ExerciseSeriesAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var exerciseSeries = listOf<ExerciseSeries>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExerciseSeriesViewHolder(
            ItemExerciseSeriesBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = exerciseSeries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ExerciseSeriesViewHolder).bind(exerciseSeries[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exerciseSeries: List<ExerciseSeries>) {
        this.exerciseSeries = exerciseSeries
        Timber.d(this.exerciseSeries.toString())
        notifyDataSetChanged()
    }

    inner class ExerciseSeriesViewHolder(private val binding: ItemExerciseSeriesBinding) :
        ViewHolder(binding.root), SeriesAdapter.OnDeleteSeriesCallback {
        private val seriesAdapter = SeriesAdapter(binding.root.context, this)

        private lateinit var exerciseSeries: ExerciseSeries
        fun bind(exerciseSeries: ExerciseSeries) {
            this.exerciseSeries = exerciseSeries
            binding.exerciseSeries = this.exerciseSeries

            seriesAdapter.updateList(exerciseSeries.series)
            binding.series.adapter = seriesAdapter

            binding.series.layoutParams.height = (exerciseSeries.series.size * 30).dp
            binding.executePendingBindings()

            binding.addSeries.setOnClickListener {
                exerciseSeries.series = exerciseSeries.series + Series(Random.nextInt(1, 100), Random.nextInt(1, 100))
                seriesAdapter.updateList(exerciseSeries.series)
                resizeSeriesListView()
            }
        }

        override fun onDeleteSeries(indexOfSeries: Int) {
            val mutableSeries = exerciseSeries.series.toMutableList()
            mutableSeries.removeAt(indexOfSeries)
            exerciseSeries.series = mutableSeries

            seriesAdapter.updateList(exerciseSeries.series)
            resizeSeriesListView()
        }

        private fun resizeSeriesListView() {
            Timber.d("Resize series list view: Series count: ${exerciseSeries.series.size}")
            if (seriesAdapter.count >= 20)
                binding.series.layoutParams.height = exerciseSeries.series.size * 31.dp
            else
                binding.series.layoutParams.height = exerciseSeries.series.size * 30.dp
        }
    }
}
