package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.settings.Units
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemExerciseSeriesBinding
import com.kwasowski.sportslife.extensions.dp
import com.kwasowski.sportslife.utils.UnitsTag

class ExerciseSeriesAdapter(
    private val isDetailsView: Boolean,
    private val unitSettings: Units,
    private val onExerciseTitleClick: (ExerciseSeries) -> Unit,
) :
    RecyclerView.Adapter<ViewHolder>() {
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

    @SuppressLint("NotifyDataSetChanged")
    fun removeAt(position: Int) {
        this.exerciseSeries.removeAt(position)
        notifyDataSetChanged()
    }

    fun getAll(): List<ExerciseSeries> = this.exerciseSeries

    inner class ExerciseSeriesViewHolder(private val binding: ItemExerciseSeriesBinding) :
        ViewHolder(binding.root), SeriesAdapter.OnSeriesCallback {

        private lateinit var seriesAdapter: SeriesAdapter
        private lateinit var exerciseSeries: ExerciseSeries
        fun bind(exerciseSeries: ExerciseSeries) {
            this.exerciseSeries = exerciseSeries
            binding.exerciseSeries = this.exerciseSeries
            binding.exerciseName.text = exerciseSeries.exerciseName

            seriesAdapter = SeriesAdapter(binding.root.context, this, isDetailsView)

            seriesAdapter.addAll(exerciseSeries.series.toMutableList())
            binding.series.adapter = seriesAdapter

            binding.exerciseName.setOnClickListener {
                onExerciseTitleClick(exerciseSeries)
            }

            binding.exerciseSeriesBar.setOnClickListener {
                onExerciseTitleClick(exerciseSeries)
            }

            when (exerciseSeries.units) {
                UnitsTag.MEASURE -> when (unitSettings) {
                    Units.KG_M -> seriesAdapter.units = " m"
                    Units.LBS_MI -> seriesAdapter.units = " mi"
                }

                UnitsTag.WEIGHT -> when (unitSettings) {
                    Units.KG_M -> seriesAdapter.units = " kg"
                    Units.LBS_MI -> seriesAdapter.units = " lbs"
                }

                UnitsTag.NONE -> Unit
            }

            if (isDetailsView) {
                binding.addSeries.visibility = View.GONE
                binding.deleteExerciseSeries.visibility = View.GONE
            } else {
                binding.addSeries.setOnClickListener {
                    seriesAdapter.add(Series())
                    this.exerciseSeries.series = seriesAdapter.getAll()
                    resizeSeriesListView()
                }

                binding.deleteExerciseSeries.setOnClickListener {
                    removeAt(layoutPosition)
                }
            }

            resizeSeriesListView()
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
