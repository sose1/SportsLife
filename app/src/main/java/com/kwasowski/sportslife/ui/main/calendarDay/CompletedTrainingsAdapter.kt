package com.kwasowski.sportslife.ui.main.calendarDay

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.ItemTrainingMainActivityBinding

class CompletedTrainingsAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var trainings = listOf<Training>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CompletedTrainingViewHolder(
            ItemTrainingMainActivityBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = trainings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as CompletedTrainingViewHolder).bind(trainings[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(trainings: List<Training>) {
        this.trainings = trainings
        notifyDataSetChanged()
    }

    inner class CompletedTrainingViewHolder(private val binding: ItemTrainingMainActivityBinding) :
        ViewHolder(binding.root) {

            fun bind(training: Training) {
                binding.training = training
                binding.executePendingBindings()
            }
    }
}


