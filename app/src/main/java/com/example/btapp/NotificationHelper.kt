package com.example.btapp
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Class that sets up notification functionality
 */
class NotificationHelper(private val context: Context) {
    private val channelId = "BTAppChannel"
    private val notificationId = 1;


    @SuppressLint("MissingPermission")
    fun sendNotification(title:String, message: String){
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }

    }

}