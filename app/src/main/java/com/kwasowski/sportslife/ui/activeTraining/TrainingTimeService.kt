package com.kwasowski.sportslife.ui.activeTraining

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.ui.activeTraining.activity.ActiveTrainingActivity.Companion.DURATION_OF_TRAINING_KEY
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date

class TrainingTimeService : Service(), CoroutineScope {

    private val job = Job()
    override val coroutineContext = Dispatchers.Default + job

    private var startTime = System.currentTimeMillis()

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        createNotificationChannel()

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.light_primary))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification))
            .setContentTitle(getString(R.string.duration_of_training))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime
                val formattedTime = formatTime(elapsedTime)
                val durationOfTrainingReceiverIntent = Intent("com.kwasowski.sportslife.durationOfTraining")

                durationOfTrainingReceiverIntent.putExtra(DURATION_OF_TRAINING_KEY, formattedTime)
                sendBroadcast(durationOfTrainingReceiverIntent)

                if (ContextCompat.checkSelfPermission(
                        this@TrainingTimeService,
                        Manifest.permission.FOREGROUND_SERVICE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationBuilder.setContentText(formattedTime)
                    val notification = notificationBuilder.build()
                    notificationManager.notify(DURATION_OF_TRAINING_NOTIFICATION_ID, notification)
                }

                delay(1000)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        job.cancel()
        notificationManager.cancel(DURATION_OF_TRAINING_NOTIFICATION_ID)
        super.onDestroy()
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(milliseconds: Long): String {
        val dateFormat = SimpleDateFormat("H'h' m'min' s's'")
        val formattedTime = dateFormat.format(Date(milliseconds))

        return formattedTime.replace("0h ", "").replace("0min ", "")
    }

    private fun createNotificationChannel() {
        val name = "Duration of Training"
        val descriptionText = "Duration of Training"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val DURATION_OF_TRAINING_NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Duration of Training"
    }
}

