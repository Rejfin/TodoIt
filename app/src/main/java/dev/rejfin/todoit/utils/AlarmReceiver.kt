package dev.rejfin.todoit.utils

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.common.hash.Hashing
import dev.rejfin.todoit.MainActivity
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.TaskModel

class AlarmReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context?, p1: Intent?) {
        val task = p1?.getSerializableExtra("task_info") as? TaskModel
        // tapResultIntent gets executed when user taps the notification
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity( context, hashCode(task!!.id),tapResultIntent,FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val pref = context?.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
        pref?.edit()?.remove(task.id)?.apply()

        val notification = context?.let {
            NotificationCompat.Builder(it, "todoit")
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = context?.let { NotificationManagerCompat.from(it) }
        notification?.let {
            task.let {
                    it1 -> notificationManager?.notify(0, it)
            }
        }
    }

    private fun hashCode(string: String): Int {
        return Hashing.murmur3_32_fixed()
            .newHasher()
            .putUnencodedChars(string)
            .hash()
            .asInt()
    }
}