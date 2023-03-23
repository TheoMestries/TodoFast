package com.tm.todofast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("WAAAAAAAAAAAW")

        Notifications.createTaskLateNotification(
            context,
            intent.getLongExtra("taskId", -1).toInt(),
            intent.getStringExtra("taskTitle")!!
        )
    }
}