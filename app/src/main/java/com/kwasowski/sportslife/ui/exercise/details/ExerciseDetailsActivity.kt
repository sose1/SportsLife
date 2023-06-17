package com.kwasowski.sportslife.ui.exercise.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityExerciseDetailsBinding
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ExerciseDetailsActivity : AppCompatActivity() {
    private val viewModel: ExerciseDetailsViewModel by viewModel()
    private lateinit var binding: ActivityExerciseDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_details)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        onViewStateChanged()
        setVisibilityOfEditAndDeleteIcon()

        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.topAppBar.menu.getItem(0)
            .setOnMenuItemClickListener { onEditCLickListener.onMenuItemClick(it) }
        binding.topAppBar.menu.getItem(1)
            .setOnMenuItemClickListener { onDeleteClickListener.onMenuItemClick(it) }
        binding.topAppBar.menu.getItem(2)
            .setOnMenuItemClickListener { onMoreClickListener.onMenuItemClick(it) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setDefaultState()
        viewModel.getExercise(getExerciseIdFromIntent())
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    ExerciseDetailsState.Default -> Unit
                    ExerciseDetailsState.OnFailure -> showToast(R.string.network_connection_error_please_try_again_later)
                    ExerciseDetailsState.OnSuccessGet -> {
                        if (viewModel.videoLink.value.isNullOrBlank()) {
                            binding.exerciseVideoTitle.visibility = View.GONE
                            binding.exerciseVideo.visibility = View.GONE
                        } else {
                            binding.exerciseVideoTitle.visibility = View.VISIBLE
                            binding.exerciseVideo.visibility = View.VISIBLE
                        }
                    }

                    is ExerciseDetailsState.OnVideoClick -> openVideoLink(it.videoLink)
                    ExerciseDetailsState.OnSuccessDelete -> {
                        showToast(R.string.correctly_deleted_exercise)
                        finish()
                    }

                    ExerciseDetailsState.OnSuccessCopy -> showToast(R.string.success_copy_to_own)
                    ExerciseDetailsState.OnSuccessSharedExercise -> showToast(R.string.exercise_was_successfully_shared)

                }
            }
        }
    }

    private fun setVisibilityOfEditAndDeleteIcon() {
        intent.getBooleanExtra(Constants.IS_COMMUNITY_INTENT, false).let {
            if (it) {
                binding.topAppBar.menu.getItem(0).isVisible = false
                binding.topAppBar.menu.getItem(1).isVisible = false
            }
        }
    }

    private fun getExerciseIdFromIntent() = intent.getStringExtra(Constants.EXERCISE_ID_INTENT)

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }

    private fun openVideoLink(videoLink: String?) {
        viewModel.setDefaultState()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
        startActivity(browserIntent)
    }

    private val onEditCLickListener = MenuItem.OnMenuItemClickListener {
        val intent = Intent(this, ExerciseFormActivity::class.java)
        intent.putExtra(Constants.EXERCISE_ID_INTENT, viewModel.exerciseDtoMutableLiveData.value?.id)
        startActivity(intent)
        true
    }

    private val onDeleteClickListener = MenuItem.OnMenuItemClickListener {
        viewModel.deleteExercise()
        true
    }

    private val onMoreClickListener = MenuItem.OnMenuItemClickListener {
        createPopupMenu()
        true
    }

    @SuppressLint("DiscouragedPrivateApi", "RtlHardcoded")
    private fun createPopupMenu() {
        val popupMenu = PopupMenu(this, binding.topAppBar)
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener)

        val menuResId = if (intent.getBooleanExtra(Constants.IS_COMMUNITY_INTENT, false)) {
            R.menu.communities_exercise
        } else {
            R.menu.own_exercise_details
        }

        popupMenu.menuInflater.inflate(menuResId, popupMenu.menu)
        popupMenu.gravity = Gravity.RIGHT

        try {
            val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldPopup.isAccessible = true

            val mPopup = fieldPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Timber.e("PopupMenu", e)
        }

        popupMenu.show()
    }

    private val onMenuItemClickListener = PopupMenu.OnMenuItemClickListener {
        run {
            when (it.itemId) {
                R.id.add_to_training -> {
                    Timber.d("Add to training")
                }

                R.id.add_to_fav -> {
                    Timber.d("Add to fav")
                }

                R.id.share -> {
                    Timber.d("Share")
                    viewModel.shareExercise()
                }

                R.id.copy_to_own -> {
                    Timber.d("Copy")
                    viewModel.copyToOwn()
                }
            }
            true
        }
    }
}