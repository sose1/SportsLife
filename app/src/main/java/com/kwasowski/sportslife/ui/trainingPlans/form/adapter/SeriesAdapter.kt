package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemSeriesBinding

class SeriesAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var series = listOf<Series>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SeriesViewHolder(
            ItemSeriesBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = series.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as SeriesViewHolder).bind(series[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(series: List<Series>) {
        this.series = series
        notifyDataSetChanged()
    }

    inner class SeriesViewHolder(private val binding: ItemSeriesBinding) :
        ViewHolder(binding.root) {
        private lateinit var series: Series
        fun bind(series: Series) {
            this.series = series
            binding.series = this.series
            binding.executePendingBindings()
        }
    }
}