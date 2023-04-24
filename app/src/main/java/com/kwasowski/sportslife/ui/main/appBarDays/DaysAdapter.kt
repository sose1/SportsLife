package com.kwasowski.sportslife.ui.main.appBarDays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.model.Day
import com.kwasowski.sportslife.data.model.DayType
import com.kwasowski.sportslife.databinding.ItemDayActiveBinding
import com.kwasowski.sportslife.databinding.ItemDayCurrentBinding
import com.kwasowski.sportslife.databinding.ItemDayDefaultBinding

class DaysAdapter(private val onSelect: (Day) -> Unit) : RecyclerView.Adapter<ViewHolder>() {
    private var daysList = listOf<Day>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            DayType.DEFAULT.value -> DayDefaultViewHolder(
                ItemDayDefaultBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            DayType.CURRENT.value -> DayCurrentViewHolder(
                ItemDayCurrentBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            DayType.ACTIVE.value -> DayActiveViewHolder(
                ItemDayActiveBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> DayDefaultViewHolder(
                ItemDayDefaultBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = daysList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DayType.DEFAULT.value -> (holder as DayDefaultViewHolder).bind(
                daysList[position], onSelect
            )

            DayType.CURRENT.value -> (holder as DayCurrentViewHolder).bind(
                daysList[position]
            )

            DayType.ACTIVE.value -> (holder as DayActiveViewHolder).bind(
                daysList[position]
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return daysList[position].type.value
    }

    fun updateList(daysList: List<Day>) {
        this.daysList = daysList
        notifyItemInserted(itemCount)
    }

    inner class DayDefaultViewHolder(private val binding: ItemDayDefaultBinding) :
        ViewHolder(binding.root) {

        fun bind(day: Day, onSelect: (Day) -> Unit) {
            day.position = adapterPosition
            binding.day = day
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                onSelect(day)
            }
        }
    }

    inner class DayActiveViewHolder(private val binding: ItemDayActiveBinding) :
        ViewHolder(binding.root) {

        fun bind(day: Day) {
            day.position = adapterPosition
            binding.day = day
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onSelect(day)
            }
        }
    }

    inner class DayCurrentViewHolder(private val binding: ItemDayCurrentBinding) :
        ViewHolder(binding.root) {

        fun bind(day: Day) {
            day.position = adapterPosition
            binding.day = day
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onSelect(day)
            }
        }
    }
}