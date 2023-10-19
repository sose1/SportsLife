package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.databinding.ItemTrainingPlanBinding
import timber.log.Timber

class OwnTrainingPlansAdapter(
    private val context: Context,
    private val canAddTrainingToCalendarDay: Boolean,
    private val onMenuItemSelected: (TrainingPlanDto, Int) -> Unit,
    private val onItemClick: (TrainingPlanDto) -> Unit,
    private val onScheduleButtonClicked: (TrainingPlanDto) -> Unit,
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
            if (canAddTrainingToCalendarDay) {
                binding.startButton.visibility = View.GONE
                binding.scheduleButton.setOnClickListener {
                    onScheduleButtonClicked(trainingPlan)
                }
            } else {
                binding.scheduleButton.visibility = View.GONE
            }

            binding.moreButton.setOnClickListener {
                createPopupMenu()
            }
            binding.executePendingBindings()
        }

        @SuppressLint("DiscouragedPrivateApi", "RtlHardcoded")
        private fun createPopupMenu() {
            val popupMenu = PopupMenu(context, binding.moreButton)
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener)
            popupMenu.menuInflater.inflate(R.menu.own_training_plan, popupMenu.menu)
            popupMenu.gravity = Gravity.RIGHT

            try {
                val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopup.isAccessible = true

                val mPopup = fieldPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Timber.e("PopupMenu", e)
            }

            popupMenu.show()
        }

        private val onMenuItemClickListener = PopupMenu.OnMenuItemClickListener {
            run {
                onMenuItemSelected(this.trainingPlan, it.itemId)
                true
            }
        }
    }
}