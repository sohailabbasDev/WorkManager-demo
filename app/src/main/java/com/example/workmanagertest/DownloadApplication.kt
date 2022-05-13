package com.example.workmanagertest

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager


import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES

class DownloadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//Creating a Notification Channel
        if (SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Image Download",
                NotificationManager.IMPORTANCE_HIGH

            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}