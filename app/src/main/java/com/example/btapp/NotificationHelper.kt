package com.example.btapp
import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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