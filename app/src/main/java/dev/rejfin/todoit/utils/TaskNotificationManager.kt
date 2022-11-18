package dev.rejfin.todoit.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.common.hash.Hashing
import dev.rejfin.todoit.models.TaskModel

class TaskNotificationManager {
    fun setAlarm(context: Context, task: TaskModel, groupName: String? = null) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, groupName)
        }

        val pref = context.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
        pref.edit().putBoolean(task.id, true).apply()
        val beforeTime = pref.getInt("notification_time", 15)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("task_info", task)
        val pendingIntent = PendingIntent.getBroadcast(context, hashCode(task.id), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC, task.startTimestamp - beforeTime*60*1000, pendingIntent)
    }

    fun removeAlarm(context: Context, task: TaskModel){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        // It is not necessary to add putExtra
        intent.putExtra("task_info", task)
        val pendingIntent = PendingIntent.getBroadcast(context, hashCode(task.id), intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        val pref = context.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
        pref.edit().remove(task.id).apply()
    }

    fun isAlarmExisting(context: Context, task: TaskModel): Boolean{
        val pref = context.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
        return pref.contains(task.id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, groupName: String? = null) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("todoit", groupName ?: "User Tasks Notification", importance).apply {
            description = if(groupName != null) "Notification for tasks from group: $groupName" else "Notification for Tasks in user private list"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun hashCode(string: String): Int {
        return Hashing.murmur3_32_fixed()
            .newHasher()
            .putUnencodedChars(string)
            .hash()
            .asInt()
    }
}