package io.github.jing.work.assistant.receiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import com.baidu.geofence.GeoFence
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.ext.createChannel
import io.github.jing.work.assistant.ext.showNotification
import io.github.jing.work.assistant.openQW

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "${intent.action} arrived")
        if (ACTION_GEOFENCE == intent.action) {
            //围栏行为
            val fenceStatus = intent.getIntExtra(GeoFence.BUNDLE_KEY_FENCESTATUS, GeoFence.STATUS_UNKNOWN)
            val fenceId = intent.getStringExtra(GeoFence.BUNDLE_KEY_FENCEID);
            val customId = intent.getStringExtra(GeoFence.BUNDLE_KEY_CUSTOMID)
            Log.i(TAG, "fenceId ${fenceId}, customId ${customId}, fenceStatus: $fenceStatus")
            val workManager = WorkManager.getInstance(context)
            if (GeoFence.STATUS_IN == fenceStatus) {
                //进入
                context.showNotification(createNotification(context, "进入"))
                openQW(context)
            } else if (GeoFence.STATUS_STAYED == fenceStatus) {
                //停留
                context.showNotification(createNotification(context, "停留在范围内"))
                openQW(context)
            } else if (GeoFence.STATUS_OUT == fenceStatus) {
                //退出
                context.showNotification(createNotification(context, "离开"))
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
        const val TAG = "GeofenceReceiver"
        const val ACTION_GEOFENCE = "io.github.jing.work.assistant.GEOFENCE"
    }
}