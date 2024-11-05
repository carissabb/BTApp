package com.example.btapp
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Class that sets up notification functionality
 */
class NotificationHelper(private val context: Context) {
    private val channelId = "BTAppChannel"
    private val notificationId = 1;

    init{
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "BTApp Notifications"
        val descriptionText = "Channel for BTApp notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

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