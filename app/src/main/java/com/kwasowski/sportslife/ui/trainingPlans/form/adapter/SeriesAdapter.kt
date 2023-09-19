package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemSeriesBinding

class SeriesAdapter(context: Context, private val onDeleteSeriesCallback: OnDeleteSeriesCallback) :
    ArrayAdapter<Series>(context, 0) {
    private var series = emptyList<Series>()

    fun updateList(series: List<Series>) {
        this.series = series
        notifyDataSetChanged()
    }

    fun getSeries(): List<Series> = series

    override fun getCount(): Int = series.size

    override fun getItem(position: Int): Series = series[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemSeriesBinding
        val view: View

        if (convertView == null) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_series,
                parent, false
            )

            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemSeriesBinding
            view = convertView
        }

        val seriesItem = getItem(position)
        binding.series = seriesItem

        binding.index.text = (position + 1).toString()
        binding.value.text = getItem(position).value.toString()
        binding.repeats.text = getItem(position).repeats.toString() + " powt"

        if (position != count - 1) {
            binding.deleteSeries.visibility = View.INVISIBLE
        } else {
            binding.deleteSeries.visibility = View.VISIBLE
        }

        binding.deleteSeries.setOnClickListener {
            onDeleteSeriesCallback.onDeleteSeries(position)
        }
        return view
    }

    interface OnDeleteSeriesCallback {
        fun onDeleteSeries(indexOfSeries: Int)
    }
}
