package com.maxent.blockadoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import java.util.Locale

class TimerService : Service() {
    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "channelID"
    }
    private lateinit var timerViewModel: TimerViewModel

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    override fun onCreate() {
        super.onCreate()
        timerViewModel = TimerViewModel()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("starting", "Starting notification")
        createNotificationChannel()
        showNotification()
        return START_STICKY // If the service is killed, it will be automatically restarted
    }

    private fun showNotification() {
//        val notification = createNotification()
//        startForeground(1, notification)

        val notificationText = timerViewModel.formatTime(timerViewModel.currentTimeLeft)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Blockadoro")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.notify(NOTIFICATION_ID, builder.build()) //todo notification
    }
    private fun createNotificationChannel() {
        // Create a notification channel (required for API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Blockadoro Timer",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
