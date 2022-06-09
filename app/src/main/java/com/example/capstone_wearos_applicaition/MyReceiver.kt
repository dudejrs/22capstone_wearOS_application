package com.example.capstone_wearos_applicaition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.action)){
            var myintent : Intent = Intent(context, NotificationManagerService::class.java)
            myintent.putExtra("action","CREATE_NOTIFICATION_CHANNEL")

            context.startService(myintent)
        }
    }

}