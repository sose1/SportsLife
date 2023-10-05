package com.kwasowski.sportslife.ui.trainingPlans.form

import ParcelableMutableList
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.trainingPlan.ExerciseSeries
import com.kwasowski.sportslife.databinding.ActivityTrainingPlanFormBinding
import com.kwasowski.sportslife.extensions.parcelable
import com.kwasowski.sportslife.ui.exercise.exerciseList.activity.ExercisesListActivity
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormActivity
import com.kwasowski.sportslife.ui.trainingPlans.form.adapter.ExerciseSeriesAdapter
import com.kwasowski.sportslife.utils.ActivityOpenMode
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TrainingPlanFormActivity : AppCompatActivity() {
    private val viewModel: TrainingPlanFormViewModel by viewModel()
    private lateinit var binding: ActivityTrainingPlanFormBinding
    private lateinit var exerciseSeriesAdapter: ExerciseSeriesAdapter
    private var refreshData = true

    enum class BundleFormKey {
        NAME, DESCRIPTION
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            refreshData = false
            Timber.d("ResultCode: ${result.resultCode}")
            if (result.resultCode == RESULT_OK) {
                val receivedData =
                    result?.data?.parcelable<Parcelable>(Constants.EXERCISES_TO_ADD) as ParcelableMutableList<*>
                val exerciseSeriesList = receivedData.map {
                    ExerciseSeries(it["id"].toString(), it["name"].toString())
                }
                exerciseSeriesAdapter.addAll(exerciseSeriesList)
                binding.nestedScrollView.post {
                    binding.nestedScrollView.smoothScrollTo(
                        0,
                        binding.nestedScrollView.getChildAt(0).height
                    )
                }
            }
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

        exerciseSeriesAdapter = ExerciseSeriesAdapter(isDetailsView())
        binding.exercisesSeries.adapter = exerciseSeriesAdapter


        if (isDetailsView())
            setDetailsView()
        onViewStateChanged()
    }

    override fun onResume() {
        super.onResume()
        if (refreshData) {
            viewModel.setStateToDefault()
            viewModel.getTrainingPlanById(getTrainingPlanIdFromIntent())
            setOnSharedChangeListener()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLauncher.unregister()
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


    private fun getTrainingPlanIdFromIntent() =
        intent.getStringExtra(Constants.TRAINING_PLAN_ID_INTENT)

    private fun isDetailsView() =
        intent.getBooleanExtra(Constants.TRAINING_PLAN_IS_DETAILS_VIEW, false)

    private fun setDetailsView() {
        binding.topAppBar.title = getString(R.string.details)
        binding.buttons.visibility = View.GONE
        binding.addExerciseSeriesButton.visibility = View.GONE
        binding.sharedGroup.visibility = View.GONE
        binding.sharedText.visibility = View.GONE
        binding.trainingPlanName.focusable = View.NOT_FOCUSABLE
        binding.trainingPlanDescription.focusable = View.NOT_FOCUSABLE
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    TrainingPlanFormState.Default -> Unit
                    TrainingPlanFormState.OnDescriptionEmptyError -> showInputError(
                        binding.trainingPlanDescriptionInputLayout,
                        R.string.empty_training_plan_description_error_message
                    )

                    TrainingPlanFormState.OnDescriptionLengthLimitError -> showInputError(
                        binding.trainingPlanDescriptionInputLayout,
                        R.string.too_long_training_plan_description_error_message
                    )

                    TrainingPlanFormState.OnNameEmptyError -> showInputError(
                        binding.trainingPlanNameInputLayout,
                        R.string.empty_training_plan_name_error_message
                    )

                    TrainingPlanFormState.OnNameLengthLimitError -> showInputError(
                        binding.trainingPlanNameInputLayout,
                        R.string.too_long_training_plan_name_error_message
                    )

                    TrainingPlanFormState.OnError -> showSnackBarInfo(R.string.network_connection_error_please_try_again_later)
                    TrainingPlanFormState.OnSuccessSave -> {
                        showToast(R.string.success_save_training_plan)
                        finish()
                    }

                    TrainingPlanFormState.OnSearchExerciseButtonClicked -> openExerciseListActivity()
                    TrainingPlanFormState.OnSuccessGet -> {
                        if (!isDetailsView())
                            binding.topAppBar.title = getString(R.string.editing)

                        viewModel.shared.value?.let {shared ->
                            binding.sharedGroup.check(
                                if (shared) R.id.yes else R.id.no
                            )
                        } ?: binding.sharedGroup.check(R.id.no)


                        exerciseSeriesAdapter.updateList(
                            viewModel.exerciseSeries.value ?: emptyList()
                        )
                        viewModel.setStateToDefault()
                    }

                    TrainingPlanFormState.ReadExerciseSeries -> {
                        viewModel.exerciseSeries.value = exerciseSeriesAdapter.getAll()
                        viewModel.setStateToDefault()
                        viewModel.saveTrainingPlan()
                    }
                }
            }
        }
    }

    private fun openExerciseListActivity() {
        viewModel.setStateToDefault()
        val intent = Intent(this, ExercisesListActivity::class.java)
        intent.putExtra(
            Constants.OPEN_MODE,
            ActivityOpenMode.ADD_EXERCISE_TO_TRAINING_PLAN as Parcelable
        )
        activityResultLauncher.launch(intent)
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

    private fun setOnSharedChangeListener() {
        binding.sharedGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.yes -> viewModel.shared.value = true
                R.id.no -> viewModel.shared.value = false
            }
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