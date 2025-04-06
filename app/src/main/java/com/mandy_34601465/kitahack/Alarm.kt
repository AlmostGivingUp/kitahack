package com.mandy_34601465.kitahack


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*



class AlarmScheduler(private val context: Context) {
    fun shouldScheduleReminderFromResponse(response: String): Boolean {
        //Triggered only if a reminder is set.
        val triggerPhrases = listOf(
            "I'll remind you",
            "Righty tighty.",
            "reminder set",
            "setting a reminder",
            "okay, i've added your reminder"
        )
        return triggerPhrases.any { response.contains(it, ignoreCase = true) }
    }


    //TODO: Handle edge cases of different days/times and such

    fun handleAIResponse(responseText: String, context: Context): Unit? {
            val lines = responseText.lines()
            val dateLine = lines.find { it.startsWith("Date:") }?.removePrefix("Date:")?.trim()
            val timeLine = lines.find { it.startsWith("Time:") }?.removePrefix("Time:")?.trim()
            val medLine = lines.find { it.startsWith("Medication:") }?.removePrefix("Medication:")?.trim()

            if (dateLine != null && timeLine != null && medLine != null) {
                val (year, month, day) = dateLine.split("-").map { it.toInt() }
                val (hour, minute) = timeLine.split(":").map { it.toInt() }
                println("no null")
                val scheduler = AlarmScheduler(context)
                return scheduler.scheduleReminder(
                    date = day,
                    month = month,
                    year = year,
                    hour = hour,
                    minute = minute,
                    title = "Medication reminder!",
                    message = medLine //medication name
                )



            }
            return null
    }

    private fun scheduleReminder(date: Int, month: Int, year: Int, hour: Int, minute: Int, title: String, message: String) {
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month) // Note: 0 = January
            set(Calendar.DAY_OF_MONTH, date)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmTime.timeInMillis.toInt(), // unique requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            pendingIntent
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,  // This makes the alarm repeat every 24 hours
            pendingIntent
        )
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val message = intent.getStringExtra("message") ?: "Time's up!"

        // Create notification channel
        val channelId = "reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder!"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build and show notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}