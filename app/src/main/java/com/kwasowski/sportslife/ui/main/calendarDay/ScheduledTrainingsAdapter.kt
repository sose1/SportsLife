package com.kwasowski.sportslife.ui.main.calendarDay

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.ItemTrainingMainActivityBinding

class ScheduledTrainingsAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var trainings = listOf<Training>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ScheduledTrainingViewHolder(
            ItemTrainingMainActivityBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = trainings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ScheduledTrainingViewHolder).bind(trainings[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(trainings: List<Training>) {
        this.trainings = trainings
        notifyDataSetChanged()
    }

    inner class ScheduledTrainingViewHolder(private val binding: ItemTrainingMainActivityBinding) :
        ViewHolder(binding.root) {

        fun bind(training: Training) {
            binding.training = training
            binding.executePendingBindings()
        }
    }
}


