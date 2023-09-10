package com.kwasowski.sportslife.ui.trainingPlans.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityTrainingPlanFormBinding
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: 1. Pobranie listy ćwiczeń own i shared (dopsianie adapterów)
// TODO: 2. Edycja ćwiczenia - pobranie danych ćwieczenia po id
// TODO: 3. Obsługa wyszikwaiania ćwiczenia i dodawania go do treningu (obsłgua przekazywania id ćwieczenia)
// todo: 4. Obśługa dodwania/ usuwania serii ćwiczeń
class TrainingPlanFormActivity : AppCompatActivity() {
    private val viewModel: TrainingPlanFormViewModel by viewModel()
    private lateinit var binding: ActivityTrainingPlanFormBinding

    enum class BundleFormKey {
        NAME, DESCRIPTION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_plan_form)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.cancelButton.setOnClickListener { finish() }

        binding.nameInputLimit = TrainingPlanFormViewModel.InputLengthLimit.NAME
        binding.descriptionInputLimit = TrainingPlanFormViewModel.InputLengthLimit.DESCRIPTION

        binding.trainingPlanName.addTextChangedListener(nameEditTextWatcher)
        binding.trainingPlanDescription.addTextChangedListener(descriptionEditTextWatcher)

        onViewStateChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            BundleFormKey.NAME.toString(), binding.trainingPlanName.text.toString()
        )
        outState.putString(
            BundleFormKey.DESCRIPTION.toString(), binding.trainingPlanDescription.text.toString()
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.trainingPlanName.setText(savedInstanceState.getString(ExerciseFormActivity.BundleFormKey.NAME.toString()))
        binding.trainingPlanDescription.setText(savedInstanceState.getString(ExerciseFormActivity.BundleFormKey.DESCRIPTION.toString()))
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    TrainingPlanFormState.Default -> Unit
                    TrainingPlanFormState.OnDescriptionEmptyError -> showInputError(binding.trainingPlanDescriptionInputLayout, R.string.empty_training_plan_description_error_message)
                    TrainingPlanFormState.OnDescriptionLengthLimitError -> showInputError(binding.trainingPlanDescriptionInputLayout, R.string.too_long_training_plan_description_error_message)
                    TrainingPlanFormState.OnNameEmptyError -> showInputError(binding.trainingPlanNameInputLayout, R.string.empty_training_plan_name_error_message)
                    TrainingPlanFormState.OnNameLengthLimitError -> showInputError(binding.trainingPlanNameInputLayout, R.string.too_long_training_plan_name_error_message)
                    TrainingPlanFormState.OnError ->  showSnackBarInfo(R.string.network_connection_error_please_try_again_later)
                    TrainingPlanFormState.OnSuccessSave -> {
                        showToast(R.string.success_save_training_plan)
                        finish()
                    }
                }
            }
        }
    }


    private val nameEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrBlank()) {
                binding.trainingPlanNameInputLayout.error = null
                viewModel.setStateToDefault()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private val descriptionEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrBlank()) {
                binding.trainingPlanDescriptionInputLayout.error = null
                viewModel.setStateToDefault()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun showInputError(inputLayout: TextInputLayout, stringId: Int) {
        inputLayout.error = getString(stringId)
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }
}