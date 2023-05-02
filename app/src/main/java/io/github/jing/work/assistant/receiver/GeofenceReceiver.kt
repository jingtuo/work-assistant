package io.github.jing.work.assistant.receiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.openQW
import io.github.jing.work.assistant.worker.createChannel
import io.github.jing.work.assistant.worker.showNotification

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_TRIGGER_GEOFENCE == intent.action) {
            var triggerFlag = intent.getIntExtra(Constants.TRIGGER_FLAG, Constants.TRIGGER_FLAG_NONE)
            val workManager = WorkManager.getInstance(context)
            if (Constants.TRIGGER_FLAG_NONE == triggerFlag) {
                return
            }
            if (Constants.TRIGGER_FLAG_IN_RANGE == triggerFlag) {
                //进入
                context.showNotification(createNotification(context, "进入"))
                workManager.cancelUniqueWork(Constants.WORK_START_CLOCK_IN)
                openQW(context)
            } else if (Constants.TRIGGER_FLAG_STAY_IN_RANGE == triggerFlag) {
                //停留
                context.showNotification(createNotification(context, "停留在范围内"))
                workManager.cancelUniqueWork(Constants.WORK_END_CLOCK_IN)
                openQW(context)
            } else if (Constants.TRIGGER_FLAG_OUT_OF_RANGE == triggerFlag) {
                //退出
                context.showNotification(createNotification(context, "离开"))
                workManager.cancelUniqueWork(Constants.WORK_END_CLOCK_IN)
                openQW(context)
            } else if (Constants.TRIGGER_FLAG_STAY_OUT_OF_RANGE == triggerFlag) {
                //停留
                context.showNotification(createNotification(context, "停留在范围外"))
                workManager.cancelUniqueWork(Constants.WORK_END_CLOCK_IN)
                openQW(context)
            }
        }
    }

    private fun createNotification(context: Context, text: String): Notification {
        context.createChannel()
        return NotificationCompat.Builder(context, Constants.CHANNEL_ID_AUTO_CLOCK_IN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(context.getString(R.string.auto_clock_in))
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(true)
            .build()
    }

    companion object {
        const val ACTION_TRIGGER_GEOFENCE = "io.github.jing.work.assistant.TRIGGER_GEOFENCE"
    }
}