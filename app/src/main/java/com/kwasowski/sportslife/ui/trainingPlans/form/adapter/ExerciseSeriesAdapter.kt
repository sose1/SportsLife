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

class ExerciseSeriesAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var exerciseSeries = mutableListOf<ExerciseSeries>()

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
        this.exerciseSeries = exerciseSeries.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(exerciseSeries: ExerciseSeries) {
        this.exerciseSeries.add(exerciseSeries)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(exerciseSeries: List<ExerciseSeries>) {
        this.exerciseSeries.addAll(exerciseSeries)
        notifyDataSetChanged()
    }

    fun getAll(): List<ExerciseSeries> = this.exerciseSeries


    inner class ExerciseSeriesViewHolder(private val binding: ItemExerciseSeriesBinding) :
        ViewHolder(binding.root), SeriesAdapter.OnSeriesCallback {
        private val seriesAdapter = SeriesAdapter(binding.root.context, this)

        private lateinit var exerciseSeries: ExerciseSeries
        fun bind(exerciseSeries: ExerciseSeries) {
            this.exerciseSeries = exerciseSeries
            binding.exerciseSeries = this.exerciseSeries
            binding.exerciseName.text = exerciseSeries.exerciseName

            seriesAdapter.addAll(exerciseSeries.series.toMutableList())
            binding.series.adapter = seriesAdapter

            resizeSeriesListView()

            binding.addSeries.setOnClickListener {
                seriesAdapter.add(Series())
                this.exerciseSeries.series = seriesAdapter.getAll()
                resizeSeriesListView()
            }

            binding.executePendingBindings()
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
}
