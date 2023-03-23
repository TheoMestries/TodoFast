package com.tm.todofast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("WAAAAAAAAAAAW")

        Notifications.createTaskLateNotification(
            context!!,
            intent!!.getIntExtra("taskId", -1),
            intent.getStringExtra("taskTitle")!!
        )
    }
}