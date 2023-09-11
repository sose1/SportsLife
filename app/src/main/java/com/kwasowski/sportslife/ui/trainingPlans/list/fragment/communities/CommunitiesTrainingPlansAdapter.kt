package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.databinding.ItemTrainingPlanBinding

class CommunitiesTrainingPlansAdapter(
    private val onItemClick: (TrainingPlanDto) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private var trainingPlans = listOf<TrainingPlanDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TrainingPlanViewHolder(
            ItemTrainingPlanBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = trainingPlans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as TrainingPlanViewHolder).bind(trainingPlans[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(trainingPlans: List<TrainingPlanDto>) {
        this.trainingPlans = trainingPlans
        notifyDataSetChanged()
    }

    inner class TrainingPlanViewHolder(private val binding: ItemTrainingPlanBinding) :
        ViewHolder(binding.root) {
        private lateinit var trainingPlan: TrainingPlanDto
        fun bind(trainingPlan: TrainingPlanDto) {
            this.trainingPlan = trainingPlan
            binding.trainingPlan = trainingPlan
            binding.root.setOnClickListener {
                onItemClick(trainingPlan)
            }
            binding.moreButton.visibility = View.GONE
            binding.executePendingBindings()
        }
    }
}