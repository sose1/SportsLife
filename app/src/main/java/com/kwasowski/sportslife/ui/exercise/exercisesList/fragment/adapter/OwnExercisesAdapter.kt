package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.exercise.Exercise
import com.kwasowski.sportslife.databinding.ItemExerciseBinding

class OwnExercisesAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var exercises = listOf<Exercise>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(
            ItemExerciseBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ExerciseViewHolder).bind(exercises[position])

    fun updateList(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }


    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) :
        ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.executePendingBindings()
        }
    }
}