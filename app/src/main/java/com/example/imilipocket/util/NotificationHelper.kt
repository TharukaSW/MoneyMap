package com.example.imilipocket.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.imilipocket.ui.MainActivity
import com.example.imilipocket.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "budget_alerts"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Budget Alerts"
                val descriptionText = "Notifications for budget alerts"
                // Change importance to IMPORTANCE_HIGH for heads-up notifications
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableVibration(true) // Enable vibration for better visibility
                    // Optionally, set a vibration pattern
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showBudgetNotification(monthlyBudget: Double) {
        try {
            val title = "Budget Updated"
            val message = "Your monthly budget has been set to ${CurrencyFormatter.formatAmount(context, monthlyBudget)}"

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Already set correctly
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                // Optional: Add sound for better heads-up effect
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showBudgetAlert(monthlyBudget: Double, monthlyExpenses: Double) {
        try {
            val progress = if (monthlyBudget > 0) {
                (monthlyExpenses / monthlyBudget * 100).toInt()
            } else {
                0
            }

            val title = when {
                progress >= 100 -> "Budget Exceeded!"
                progress >= 90 -> "Budget Warning!"
                else -> return // Don't show notification if below 90%
            }

            val remaining = monthlyBudget - monthlyExpenses
            val message = when {
                progress >= 100 -> "You've exceeded your budget by ${CurrencyFormatter.formatAmount(context, monthlyExpenses - monthlyBudget)}"
                else -> "You have ${CurrencyFormatter.formatAmount(context, remaining)} remaining (${100 - progress}% left)"
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}