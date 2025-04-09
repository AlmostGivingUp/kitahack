package com.mandy_34601465.kitahack

import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager // Import for sound
import android.net.Uri // Import for sound
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale
import android.R
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import java.util.Calendar

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
            try {
                val (year, month, day) = dateLine.split("-").map { it.toInt() }
                val (hour, minute) = timeLine.split(":").map { it.toInt() }
                val calendarMonth = month - 1
                Log.d("AlarmScheduler", "Scheduling alarm for: $year-${calendarMonth + 1}-$day $hour:$minute, Medication: $medLine")

                val scheduler = AlarmScheduler(context)
                return scheduler.scheduleReminder(
                    date = day,
                    month = calendarMonth,
                    year = year,
                    hour = hour,
                    minute = minute,
                    medicationName = medLine
                )
            } catch (e: NumberFormatException) {
                Log.e("AlarmScheduler", "Error parsing date or time: ${e.message}", e) // Use e.message
            } catch (e: Exception) {
                Log.e("AlarmScheduler", "Error scheduling reminder: ${e.message}", e) // Use e.message
            }
        } else {
            Log.w("AlarmScheduler", "Incomplete reminder information. Date: $dateLine, Time: $timeLine, Medication: $medLine")
        }
        return null
    }

    private fun scheduleReminder(date: Int, month: Int, year: Int, hour: Int, minute: Int, medicationName: String) {
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month) // Note: 0 = January
            set(Calendar.DAY_OF_MONTH, date)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        //Check if the time is in the past. If so, schedule for the next day
        if (alarmTime.before(Calendar.getInstance())) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1)
            Log.i("AlarmScheduler", "Requested time is in the past. Scheduling for next day.")
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationName", medicationName) // pass just the medication name
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmTime.timeInMillis.toInt(), // unique requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("AlarmScheduler", "Setting alarm at: ${alarmTime.time}")

        // Set the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            pendingIntent
        )
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "medication"
        val title = "Medication Reminder!"
        val message = "Reminder to take your $medicationName medication!"
        val notificationId = (title + message).hashCode()

        Log.d("AlarmReceiver", "Received alarm: Medication - $medicationName")

        // Intent to open the app (no changes here)
        val intentToOpenApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_message", message)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intentToOpenApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // --- Notification Sound ---
        // 1. Get the default notification sound URI
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        // Build the Notification
        val channelId = "medication_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder to take your medication"
                // --- Notification Sound in Channel ---
                // 2. Set the sound URI for the notification channel
                setSound(defaultSoundUri, null) // Use default sound
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info)  // Use your icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            // --- Sound for Pre-Oreo (optional, for compatibility) ---
            // 3. Set the sound URI directly on the notification for older Android versions.
            //    This is redundant on Oreo+ if you've set the channel sound.
            .setSound(defaultSoundUri)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d("AlarmReceiver", "Sent notification with ID: $notificationId")

        // --- TTS REMOVED ---
        // The TTS section (initialization, speak call, and tts member) has been removed from here.
    }
}