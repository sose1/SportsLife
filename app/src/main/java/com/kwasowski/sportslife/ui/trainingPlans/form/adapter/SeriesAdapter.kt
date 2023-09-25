package com.kwasowski.sportslife.ui.trainingPlans.form.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemSeriesBinding

class SeriesAdapter(context: Context, private val onSeriesCallback: OnSeriesCallback) :
    ArrayAdapter<Series>(context, 0) {

    override fun add(`object`: Series?) {
        super.add(`object`)
    }
    override fun remove(`object`: Series?) {
        `object`?.let {
            it.value = 0
            it.repeats = 0
        }
        super.remove(`object`)
        onSeriesCallback.onDeleteSeries()
    }

    fun getAll(): List<Series> {
        val allItems = mutableListOf<Series>()
        for (i in 0 until count) {
            getItem(i)?.let { allItems.add(it) }
        }
        return allItems
    }

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

        if (position != count - 1) {
            binding.deleteSeries.visibility = View.INVISIBLE
        } else {
            binding.deleteSeries.visibility = View.VISIBLE
        }

        binding.deleteSeries.setOnClickListener {
            view.post {
                remove(getItem(position))
                binding.unbind()
            }
        }

        val seriesItem = getItem(position)
        binding.indexOfSeries = (position + 1).toString()
        binding.value.setText(if (seriesItem?.value == 0) "" else seriesItem?.value.toString(), TextView.BufferType.EDITABLE)
        binding.repeats.setText(if (seriesItem?.repeats == 0) "" else seriesItem?.repeats.toString(), TextView.BufferType.EDITABLE)


        binding.value.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun afterTextChanged(s: Editable?) {
                val newValue = s?.toString()?.toIntOrNull() ?: 0
                seriesItem?.value = newValue
            }
        })

        binding.repeats.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun afterTextChanged(s: Editable?) {
                val newValue = s?.toString()?.toIntOrNull() ?: 0
                seriesItem?.repeats = newValue
            }
        })

        return view
    }


    interface OnSeriesCallback {
        fun onDeleteSeries()
    }
}
