package com.kwasowski.sportslife.ui.exercise.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputLayout
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityExerciseFormBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseFormActivity : AppCompatActivity() {
    private val viewModel: ExerciseFormViewModel by viewModel()
    private lateinit var binding: ActivityExerciseFormBinding

    enum class BundleFormKey {
        NAME, DESCRIPTION, CATEGORY, VIDEO_LINK
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
    }

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
                }
            }
        }
    }

    private fun showInputError(inputLayout: TextInputLayout, stringId: Int) {
        inputLayout.error = getString(stringId)
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
    }
}