package com.kwasowski.sportslife.ui.activeTraining.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.calendar.Training
import com.kwasowski.sportslife.databinding.ActivityActiveTrainingBinding
import com.kwasowski.sportslife.ui.activeTraining.TrainingTimeService
import com.kwasowski.sportslife.ui.activeTraining.fragment.ExerciseSeriesFragment
import com.kwasowski.sportslife.ui.trainingSummary.TrainingSummaryActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions

class ActiveTrainingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        const val DURATION_OF_TRAINING_KEY = "DURATION_OF_TRAINING_KEY"
    }

    private val viewModel: ActiveTrainingViewModel by viewModel()
    private lateinit var binding: ActivityActiveTrainingBinding

    private var duration: String = ""

    private val REQUEST_POST_NOTIFICATION_PERMISSION = 1
    private val durationOfTrainingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val formattedTime = intent.getStringExtra(DURATION_OF_TRAINING_KEY)
            duration = "$formattedTime"
            binding.durationOfTrainingText.text = "$formattedTime"
        }
    }

    private var exerciseFragments: MutableList<ExerciseSeriesFragment> = mutableListOf()

    private lateinit var pagerAdapter: FragmentStateAdapter

    private inner class FragmentPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = exerciseFragments.size

        override fun createFragment(position: Int): Fragment {
            return exerciseFragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_training)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.getTraining(getDayIdFromIntent(), getTrainingIdFromIntent())
        initTimerService()
        onViewStateChanged()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmExitDialog()
            }
        })
        binding.completeTrainingButton.setOnClickListener { showConfirmExitDialog() }
    }

    override fun onDestroy() {
        val trainingTimeServiceIntent = Intent(this, TrainingTimeService::class.java)
        stopService(trainingTimeServiceIntent)
        unregisterReceiver(durationOfTrainingReceiver)
        super.onDestroy()
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    ActiveTrainingState.Default -> Unit
                    is ActiveTrainingState.OnSuccessGetTraining -> onSuccessGetTraining(it.training)
                    is ActiveTrainingState.OnSuccessSaveTraining -> openSummaryActivity(it.training)
                }
            }
        }
    }

    private fun onSuccessGetTraining(training: Training) {
        training.trainingPlan?.let { trainingPlan ->
            val fragments =
                trainingPlan.exercisesSeries.map {
                    ExerciseSeriesFragment.newInstance(it)
                }.toMutableList()
            exerciseFragments = fragments
        }

        initExerciseViewPager()
    }

    private fun completeTraining() {
        val updatedExerciseSeries = exerciseFragments.map { it.getExerciseSeries() }
        viewModel.completeTraining(getDayIdFromIntent(), updatedExerciseSeries, duration)
    }

    private fun openSummaryActivity(training: Training) {
        val intent = Intent(this, TrainingSummaryActivity::class.java)
        intent.putExtra(Constants.DAY_ID_INTENT, getDayIdFromIntent())
        intent.putExtra(Constants.TRAINING_INTENT, training)

        startActivity(intent)
        finish()
    }

    private fun getTrainingIdFromIntent() =
        intent?.getStringExtra(Constants.TRAINING_ID_INTENT) ?: ""

    private fun getDayIdFromIntent() =
        intent?.getStringExtra(Constants.DAY_ID_INTENT) ?: ""

    private fun initExerciseViewPager() {
        pagerAdapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)
        binding.exercisesViewpager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 1
        }

        binding.exercisesViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageIndicator.text = "${position + 1}/${pagerAdapter.itemCount}"
            }
        })
    }

    private fun initTimerService() {
        if (hasPostNotificationPermission()) {
            registerDurationOfTrainingReceiver()
            startTrainingService()
        } else {
            requestPostNotificationPermission()
        }

        val trainingTimeServiceIntent = Intent(this, TrainingTimeService::class.java)
        startService(trainingTimeServiceIntent)
    }

    private fun hasPostNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.hasPermissions(this, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            false
        }
    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.in_order_to_display_a_notification_with_the_duration_of_a_workout_the_app_needs_permission_to_send_notifications),
                REQUEST_POST_NOTIFICATION_PERMISSION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == REQUEST_POST_NOTIFICATION_PERMISSION) {
            startTrainingService()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode == REQUEST_POST_NOTIFICATION_PERMISSION) {
            requestPostNotificationPermission()
        }
    }

    private fun startTrainingService() {
        val trainingTimeServiceIntent = Intent(this, TrainingTimeService::class.java)
        startService(trainingTimeServiceIntent)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerDurationOfTrainingReceiver() {
        val intentFilter = IntentFilter("com.kwasowski.sportslife.durationOfTraining")
        registerReceiver(durationOfTrainingReceiver, intentFilter)
    }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.do_you_want_to_complete_the_training)
            .setMessage(R.string.if_you_end_a_workout_before_completing_all_the_exercises_the_unapproved_series_will_not_be_saved)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                completeTraining()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}