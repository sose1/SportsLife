package com.kwasowski.sportslife.ui.main.calendarDay

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.ItemTrainingMainActivityBinding
import timber.log.Timber

class ScheduledTrainingsAdapter(
    private val context: Context,
    private val onMenuItemSelected: (Training, Int) -> Unit,
    private val onItemClick: (Training) -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {
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
        private lateinit var training: Training

        fun bind(training: Training) {
            binding.training = training
            this.training = training
            binding.root.setOnClickListener {
                onItemClick(training)
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
            popupMenu.menuInflater.inflate(R.menu.scheduled_training_menu, popupMenu.menu)
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
                onMenuItemSelected(this.training, it.itemId)
                true
            }
        }
    }
}


