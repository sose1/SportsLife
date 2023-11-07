package com.kwasowski.sportslife.ui.activeTraining.fragment.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.SeriesInTraining
import com.kwasowski.sportslife.databinding.ItemSeriesInActiveTrainingBinding
import timber.log.Timber

class SeriesAdapter(
    context: Context,
    private val onSeriesCallback: OnSeriesCallback,
    private val series: List<SeriesInTraining>,
) :
    ArrayAdapter<SeriesInTraining>(context, 0, series) {

    override fun add(`object`: SeriesInTraining?) {
        super.add(`object`)
    }

    override fun remove(`object`: SeriesInTraining?) {
        `object`?.let {
            it.value = 0
            it.repeats = 0
            it.completed = false
        }
        super.remove(`object`)
        onSeriesCallback.onDeleteSeries()
    }

    fun getAll(): List<SeriesInTraining> {
        val allItems = mutableListOf<SeriesInTraining>()
        for (i in 0 until count) {
            getItem(i)?.let { allItems.add(it) }
        }
        return allItems
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Timber.d("getView: $position | $convertView")
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
        initializeCheckBox(binding.completedSeries, seriesItem)

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
        seriesItem: SeriesInTraining?,
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
        seriesItem: SeriesInTraining?,
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

    private fun initializeCheckBox(completedSeries: CheckBox, seriesItem: SeriesInTraining?) {
        completedSeries.isChecked = seriesItem?.completed ?: false
        completedSeries.setOnCheckedChangeListener { _, isChecked ->
            seriesItem?.completed = isChecked
        }
    }

    interface OnSeriesCallback {
        fun onDeleteSeries()
    }
}
