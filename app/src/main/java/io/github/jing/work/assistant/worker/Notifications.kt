package io.github.jing.work.assistant.worker

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.R

fun Context.createChannel() {
    val notificationManager = NotificationManagerCompat.from(this)
    var channel =
        notificationManager.getNotificationChannelCompat(Constants.CHANNEL_ID_AUTO_CLOCK_IN)
    if (channel == null) {
        channel = NotificationChannelCompat.Builder(
            Constants.CHANNEL_ID_AUTO_CLOCK_IN,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(applicationContext.getString(R.string.auto_clock_in))
            .build()
        notificationManager.createNotificationChannel(channel)
    }
}

@SuppressLint("MissingPermission")
fun Context.showNotification(notification: Notification) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.notify(Ids.getNotificationId(), notification)
}