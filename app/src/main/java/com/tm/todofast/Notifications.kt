package com.tm.todofast

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Notifications {
    private const val EDT_CHANGED = "lateTask"
    private const val GROUP_EDT_CHANGED = "com.tm.todofast.lategroup"


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Tache en retard"
            val description = "notification envoyé lorsqu'une tache est en retard "
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(EDT_CHANGED, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    @SuppressLint("MissingPermission")
    fun createTaskLateNotification(context: Context, taskId: Int, taskTitle: String) {
        if (!hasNotificationPermission(context)) {
            return
        }

        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("notifEdtChanged", true)) {
            return
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, EDT_CHANGED)
            .setContentTitle("Tache en retard !")
            .setContentText("La tache $taskTitle est en retard !! ")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_EDT_CHANGED)
            .setSmallIcon(R.mipmap.logo_todo)

        NotificationManagerCompat.from(context).notify(taskId, builder.build())
    }


    private fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return true

        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun forceNotificationPermission(context: Context): Boolean {
        if (hasNotificationPermission(context))
            return false

        val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS")
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        context.startActivity(intent)
        return true
    }

    /**
     * Ask for notification permission (needed for android 13+)
     */
    fun askForNotificationPermission(activity: Activity){
        //ask for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        createNotificationChannel(activity)
    }
}