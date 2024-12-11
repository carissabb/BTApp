package com.example.btapp
import android.Manifest
import android.R
import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat


class NotificationReceiver : BroadcastReceiver(){
    fun MyReceiver() {
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val intent1 = Intent(context, NewIntentService::class.java)
        context.startService(intent1)
    }
}

internal class NewIntentService : IntentService("NewIntentService") {
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.star_on)
            .setContentTitle("Last Run!")
            .setContentText("One hour until the last bus leaves!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    }

    companion object {
        private const val CHANNEL_ID = "TODO"
    }
}