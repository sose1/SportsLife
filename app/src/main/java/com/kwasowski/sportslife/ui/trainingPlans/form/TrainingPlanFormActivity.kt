package com.kwasowski.sportslife.ui.trainingPlans.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityTrainingPlanFormBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrainingPlanFormActivity : AppCompatActivity() {
    private val viewModel: TrainingPlanFormViewModel by viewModel()
    private lateinit var binding: ActivityTrainingPlanFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_plan_form)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

    }
}