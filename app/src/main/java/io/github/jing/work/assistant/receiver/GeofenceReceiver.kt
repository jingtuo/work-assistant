package io.github.jing.work.assistant.receiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.worker.createChannel
import io.github.jing.work.assistant.worker.showNotification

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_TRIGGER_GEOFENCE == intent.action) {
            val enter = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)
            var address = intent.getStringExtra(Constants.ADDRESS)
            val workManager = WorkManager.getInstance(context)
            if (enter) {
                //进入
                context.showNotification(createNotification(context, "进入: $address"))
                workManager.cancelUniqueWork(Constants.WORK_START_CLOCK_IN)
                openQW(context)
            } else {
                //退出
                context.showNotification(createNotification(context, "离开: $address"))
                workManager.cancelUniqueWork(Constants.WORK_END_CLOCK_IN)
                openQW(context)
            }
        }
    }


    private fun openQW(context: Context) {
        val intent = Intent()
        val component =
            ComponentName("com.tencent.wework", "com.tencent.wework.launch.LaunchSplashActivity")
        intent.component = component
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun createNotification(context: Context, text: String): Notification {
        context.createChannel()
        return NotificationCompat.Builder(context, Constants.CHANNEL_ID_AUTO_CLOCK_IN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(context.getString(R.string.auto_clock_in))
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setShowWhen(true)
            .build()
    }

    companion object {
        const val ACTION_TRIGGER_GEOFENCE = "io.github.jing.work.assistant.TRIGGER_GEOFENCE"
    }
}