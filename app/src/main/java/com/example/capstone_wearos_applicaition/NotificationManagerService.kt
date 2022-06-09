package com.example.capstone_wearos_applicaition

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationManagerService : IntentService("NotificationManagerService") {


    override fun onHandleIntent(intent: Intent?) {
        Log.d("app:NotificationManagerService::onHandleIntent","successfully receiving intent ")
        if (intent == null) return
        try {
            if ("CREATE_NOTIFICATION_CHANNEL" == intent!!.getStringExtra("action")) {
                createNotificationChanel()

            }
        }catch (e: InterruptedException){
            Thread.currentThread().interrupt()
        }
    }

    private fun createNotificationChanel() {

            val name = getString(R.string.chanel_name)
            val descriptionText = getString(R.string.chanel_id)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.chanel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("app::NotificationManagerService::createNotificationChannel","channel created")


    }

    fun makeNotificationBuilder() : NotificationCompat.Builder{
        return NotificationCompat.Builder(this,resources.getString(R.string.chanel_id))
    }

}