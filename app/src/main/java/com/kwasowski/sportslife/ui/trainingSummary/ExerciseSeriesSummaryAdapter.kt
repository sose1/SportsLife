package com.kwasowski.sportslife.ui.trainingSummary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeriesInTraining
import com.kwasowski.sportslife.databinding.ItemExerciseseriesSummaryBinding

class ExerciseSeriesSummaryAdapter() : RecyclerView.Adapter<ViewHolder>() {
    private var exerciseSeries = listOf<ExerciseSeriesInTraining>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExerciseSeriesSummaryViewHolder(
            ItemExerciseseriesSummaryBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = exerciseSeries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ExerciseSeriesSummaryAdapter.ExerciseSeriesSummaryViewHolder).bind(
            exerciseSeries[position]
        )

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exerciseSeries: List<ExerciseSeriesInTraining>) {
        this.exerciseSeries = exerciseSeries
        notifyDataSetChanged()
    }

    inner class ExerciseSeriesSummaryViewHolder(
        private val binding: ItemExerciseseriesSummaryBinding,
    ) : ViewHolder(binding.root) {
        fun bind(exerciseSeries: ExerciseSeriesInTraining) {
            binding.exerciseSeries = exerciseSeries
            binding.completedSeriesValue.text =
                exerciseSeries.series.filter { it.completed }.size.toString()
            binding.incompleteSeriesValue.text =
                exerciseSeries.series.filter { !it.completed }.size.toString()
            binding.executePendingBindings()
        }
    }
}