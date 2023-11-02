package com.kwasowski.sportslife.ui.activeTraining.fragment.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.Series
import com.kwasowski.sportslife.databinding.ItemSeriesInActiveTrainingBinding

class SeriesAdapter(
    context: Context,
    private val onSeriesCallback: OnSeriesCallback,
) :
    ArrayAdapter<Series>(context, 0) {

    var units = ""

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
        val binding: ItemSeriesInActiveTrainingBinding
        val view: View

        if (convertView == null) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_series_in_active_training,
                parent, false
            )

            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemSeriesInActiveTrainingBinding
            view = convertView
        }

        val seriesItem = getItem(position)
        binding.indexOfSeries = (position + 1).toString()
        initializeEditText(binding.value, ForSeriesProperty.VALUE, seriesItem)
        initializeEditText(binding.repeats, ForSeriesProperty.REPEATS, seriesItem)


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


        return view
    }

    enum class ForSeriesProperty {
        VALUE,
        REPEATS
    }

    private fun initializeEditText(
        editText: EditText,
        forSeriesProperty: ForSeriesProperty,
        seriesItem: Series?,
    ) {
        when (forSeriesProperty) {
            ForSeriesProperty.VALUE -> {
                editText.setText(
                    if (seriesItem?.value == 0) "" else seriesItem?.value.toString(),
                    TextView.BufferType.EDITABLE
                )
                setTextChangeListener(editText, forSeriesProperty, seriesItem)
            }

            ForSeriesProperty.REPEATS -> {
                editText.setText(
                    if (seriesItem?.repeats == 0) "" else seriesItem?.repeats.toString(),
                    TextView.BufferType.EDITABLE
                )
                setTextChangeListener(editText, forSeriesProperty, seriesItem)
            }
        }
    }

    private fun setTextChangeListener(
        editText: EditText,
        forSeriesProperty: ForSeriesProperty,
        seriesItem: Series?,
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nie jesteśmy zainteresowani tym etapem
            }

            override fun afterTextChanged(s: Editable?) {
                val newValue = s?.toString()?.toIntOrNull() ?: 0
                when (forSeriesProperty) {
                    ForSeriesProperty.VALUE -> seriesItem?.value = newValue
                    ForSeriesProperty.REPEATS -> seriesItem?.repeats = newValue
                }
            }
        })
    }

    interface OnSeriesCallback {
        fun onDeleteSeries()
    }
}
