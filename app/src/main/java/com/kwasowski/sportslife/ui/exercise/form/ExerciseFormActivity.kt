package com.kwasowski.sportslife.ui.exercise.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityExerciseFormBinding
import com.kwasowski.sportslife.utils.Constants
import com.kwasowski.sportslife.utils.UnitsTag
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseFormActivity : AppCompatActivity() {
    private val viewModel: ExerciseFormViewModel by viewModel()
    private lateinit var binding: ActivityExerciseFormBinding

    enum class BundleFormKey {
        NAME, DESCRIPTION, CATEGORY, VIDEO_LINK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_form)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.nameInputLimit = ExerciseFormViewModel.InputLengthLimit.NAME
        binding.descriptionInputLimit = ExerciseFormViewModel.InputLengthLimit.DESCRIPTION


        onViewStateChanged()

        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.cancelButton.setOnClickListener { finish() }

        binding.exerciseName.addTextChangedListener(nameEditTextWatcher)
        binding.exerciseDescription.addTextChangedListener(descriptionEditTextWatcher)
        binding.exerciseCategory.addTextChangedListener(categoryEditTextWatcher)
        binding.exerciseVideoLink.addTextChangedListener(videoLinkEditTextWatcher)

        initCategoryDropdownMenu()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setDefaultState()
        viewModel.getExercise(getExerciseIdFromIntent())
        setOnSharedChangeListener()
        setOnUnitsChangeListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            BundleFormKey.NAME.toString(), binding.exerciseName.text.toString()
        )
        outState.putString(
            BundleFormKey.DESCRIPTION.toString(), binding.exerciseDescription.text.toString()
        )
        outState.putString(
            BundleFormKey.CATEGORY.toString(), binding.exerciseCategory.text.toString()
        )
        outState.putString(
            BundleFormKey.VIDEO_LINK.toString(), binding.exerciseVideoLink.text.toString()
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.exerciseName.setText(savedInstanceState.getString(BundleFormKey.NAME.toString()))
        binding.exerciseDescription.setText(savedInstanceState.getString(BundleFormKey.DESCRIPTION.toString()))
        binding.exerciseCategory.setText(savedInstanceState.getString(BundleFormKey.CATEGORY.toString()))
        binding.exerciseVideoLink.setText(savedInstanceState.getString(BundleFormKey.VIDEO_LINK.toString()))
        initCategoryDropdownMenu()
    }

    private fun getExerciseIdFromIntent() = intent.getStringExtra(Constants.EXERCISE_ID_INTENT)

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    ExerciseFormState.Default -> Unit
                    ExerciseFormState.OnNameEmptyError -> showInputError(
                        binding.exerciseNameInputLayout,
                        R.string.empty_name_error_message
                    )

                    ExerciseFormState.OnDescriptionEmptyError -> showInputError(
                        binding.exerciseDescriptionInputLayout,
                        R.string.empty_description_error_message
                    )

                    ExerciseFormState.OnCategoryEmptyError -> showInputError(
                        binding.exerciseCategoryInputLayout,
                        R.string.empty_category_error_message
                    )

                    ExerciseFormState.OnVideoLinkInvalidUrlError -> showInputError(
                        binding.exerciseVideoLinkInputLayout,
                        R.string.invalid_video_link_error_message
                    )

                    ExerciseFormState.OnNameLengthLimitError -> showInputError(
                        binding.exerciseNameInputLayout,
                        R.string.name_length_error_message
                    )

                    ExerciseFormState.OnDescriptionLengthLimitError -> showInputError(
                        binding.exerciseDescriptionInputLayout,
                        R.string.description_length_error_message
                    )

                    ExerciseFormState.OnError -> showSnackBarInfo(R.string.network_connection_error_please_try_again_later)
                    ExerciseFormState.OnSuccessSave -> {
                        showToast(R.string.success_save_exercise)
                        finish()
                    }

                    ExerciseFormState.OnFailureGet -> showToast(R.string.network_connection_error_please_try_again_later)
                    ExerciseFormState.OnSuccessGet -> {
                        binding.topAppBar.title = getString(R.string.editing)
                        viewModel.shared.value?.let { shared ->
                            binding.sharedGroup.check(
                                if (shared)
                                    R.id.yes
                                else
                                    R.id.no
                            )
                        } ?: binding.sharedGroup.check(R.id.no)
                        viewModel.units.value?.let { unit ->
                            when (unit) {
                                UnitsTag.WEIGHT -> binding.unitsGroup.check(R.id.weight_units)
                                UnitsTag.MEASURE -> binding.unitsGroup.check(R.id.measure_units)
                                UnitsTag.NONE -> binding.unitsGroup.check(R.id.none_units)
                            }
                        }
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
                binding.exerciseNameInputLayout.error = null
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
                binding.exerciseDescriptionInputLayout.error = null
                viewModel.setStateToDefault()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
    private val categoryEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrBlank()) {
                binding.exerciseCategoryInputLayout.error = null
                viewModel.setStateToDefault()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
    private val videoLinkEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrBlank()) {
                binding.exerciseVideoLinkInputLayout.error = null
                viewModel.setStateToDefault()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun initCategoryDropdownMenu() {
        val categoryAdapter =
            object : ArrayAdapter<String>(this, R.layout.dropdown_item, viewModel.categoriesNames) {
                override fun getFilter(): Filter {
                    return object : Filter() {
                        override fun performFiltering(constraint: CharSequence?): FilterResults {
                            val filterResults = FilterResults()
                            filterResults.values = viewModel.categoriesNames
                            filterResults.count = viewModel.categoriesNames.size
                            return filterResults
                        }

                        override fun publishResults(
                            constraint: CharSequence?,
                            results: FilterResults?
                        ) {
                            notifyDataSetChanged()
                        }

                    }
                }
            }

        binding.exerciseCategory.setAdapter(
            categoryAdapter
        )
    }

    private fun setOnSharedChangeListener() {
        binding.sharedGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.yes -> viewModel.shared.value = true
                R.id.no -> viewModel.shared.value = false
            }
        }
    }

    private fun setOnUnitsChangeListener() {
        binding.unitsGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.weight_units -> viewModel.units.value = UnitsTag.WEIGHT
                R.id.measure_units -> viewModel.units.value = UnitsTag.MEASURE
                R.id.none_units -> viewModel.units.value = UnitsTag.NONE
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