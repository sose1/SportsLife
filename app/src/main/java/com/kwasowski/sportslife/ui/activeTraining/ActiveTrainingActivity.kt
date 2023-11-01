package com.kwasowski.sportslife.ui.activeTraining

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityActiveTrainingBinding
import com.kwasowski.sportslife.ui.trainingSummary.TrainingSummaryActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions

class ActiveTrainingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        const val DURATION_OF_TRAINING_KEY = "DURATION_OF_TRAINING_KEY"
    }

    private val viewModel: ActiveTrainingViewModel by viewModel()

    private lateinit var binding: ActivityActiveTrainingBinding

    private val REQUEST_POST_NOTIFICATION_PERMISSION = 1


    private val durationOfTrainingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val formattedTime = intent.getStringExtra(DURATION_OF_TRAINING_KEY)
            binding.durationOfTrainingText.text = "$formattedTime"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_training)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (hasPostNotificationPermission()) {
            startTrainingService()
            registerDurationOfTrainingReceiver()
        } else {
            requestPostNotificationPermission()
        }

        onViewStateChanged()

        val trainingTimeServiceIntent = Intent(this, TrainingTimeService::class.java)
        startService(trainingTimeServiceIntent)

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
                }
            }
        }
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
                viewModel.onConfirmExit()
                val intent = Intent(this, TrainingSummaryActivity::class.java)
                startActivity(intent)
                finish()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}