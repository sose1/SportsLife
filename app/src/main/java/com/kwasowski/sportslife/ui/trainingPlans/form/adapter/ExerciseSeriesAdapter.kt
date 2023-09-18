package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.databinding.ItemExerciseSeriesBinding
import com.kwasowski.sportslife.extensions.dp
import timber.log.Timber

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
        ViewHolder(binding.root) {
        private lateinit var exerciseSeries: ExerciseSeries
        fun bind(exerciseSeries: ExerciseSeries) {
            this.exerciseSeries = exerciseSeries
            binding.exerciseSeries = this.exerciseSeries

            val seriesAdapter = SeriesAdapter(binding.root.context)
            seriesAdapter.updateList(exerciseSeries.series)
            binding.series.adapter = seriesAdapter

            binding.series.layoutParams.height = (exerciseSeries.series.size * 29).dp
            binding.executePendingBindings()
        }
    }
}
