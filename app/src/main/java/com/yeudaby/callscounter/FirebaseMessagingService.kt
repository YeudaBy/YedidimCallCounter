package com.yeudaby.callscounter;

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber.Forest.i

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        i("onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            val notification = message.notification
            val title = notification?.title
            val body = notification?.body

            val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.horizontal_logo)
                .setAutoCancel(true)

            // Show the notification
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}
