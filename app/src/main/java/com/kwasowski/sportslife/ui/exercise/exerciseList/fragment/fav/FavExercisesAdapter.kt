package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.databinding.ItemExerciseBinding
import timber.log.Timber

class FavExercisesAdapter(private val context: Context,
                          private val addToTrainingIsVisible: Boolean,
                          private val onMenuItemSelected: (ExerciseDto, Int) -> Unit,
                          private val onItemClick: (ExerciseDto) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var exercises = listOf<ExerciseDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ExerciseViewHolder).bind(exercises[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exercises: List<ExerciseDto>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseDto
        fun bind(exercise: ExerciseDto) {
            this.exercise = exercise
            binding.exercise = exercise
            binding.root.setOnClickListener {
                onItemClick(exercise)
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
            popupMenu.menuInflater.inflate(R.menu.fav_exercise, popupMenu.menu)
            popupMenu.gravity = Gravity.RIGHT
            popupMenu.menu.findItem(R.id.add_to_training).isVisible = addToTrainingIsVisible

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

        private val onMenuItemClickListener = OnMenuItemClickListener {
            run {
                onMenuItemSelected(this.exercise, it.itemId)
                true
            }
        }
    }
}