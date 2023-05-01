package io.github.jing.work.assistant.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.tencent.map.geolocation.TencentGeofence
import com.tencent.map.geolocation.TencentGeofenceManager
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.receiver.GeofenceReceiver

class WorkClockInWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val geofenceManager: TencentGeofenceManager
    private val notificationManager: NotificationManagerCompat
    private val latitude: Double
    private val longitude: Double
    private val address: String

    init {
        geofenceManager = TencentGeofenceManager(appContext)
        notificationManager = NotificationManagerCompat.from(appContext)
        latitude = params.inputData.getDouble(Constants.LATITUDE, 0.0)
        longitude = params.inputData.getDouble(Constants.LONGITUDE, 0.0)
        address = params.inputData.getString(Constants.ADDRESS) ?: ""
    }


    override suspend fun doWork(): Result {
        //暂定地理围栏的半径是30米, 有效期是15分钟
        val geofence = TencentGeofence.Builder()
            .setTag(Constants.GEOFENCE_CLOCK_IN)
            .setCircularRegion(latitude, longitude, 30.0F)
            .setExpirationDuration(15 * 60 * 1000)
            .setExpirationDuration(15)
            .build()
        val intent = Intent(GeofenceReceiver.ACTION_TRIGGER_GEOFENCE)
        intent.setPackage(applicationContext.packageName)
        intent.putExtra(Constants.LATITUDE, latitude)
        intent.putExtra(Constants.LONGITUDE, longitude)
        intent.putExtra(Constants.GEOFENCE_TAG, Constants.GEOFENCE_CLOCK_IN)
        intent.putExtra(Constants.ADDRESS, address)
        intent.putExtra(Constants.START_TIME, System.currentTimeMillis())
        val pIntent = PendingIntent.getBroadcast(
            applicationContext,
            Ids.getPendingIntentRequestCode(), intent,
            PendingIntent.FLAG_IMMUTABLE xor PendingIntent.FLAG_UPDATE_CURRENT
        )
        geofenceManager.addFence(geofence, pIntent)
        setForeground(createForeground())
        return Result.success()
    }

    private fun createForeground(): ForegroundInfo {
        return ForegroundInfo(Ids.getNotificationId(), createNotification())
    }

    private fun createNotification(): Notification {
        applicationContext.createChannel()
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID_AUTO_CLOCK_IN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(applicationContext.getString(R.string.auto_clock_in))
            .setContentText(applicationContext.getText(R.string.positioning))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setShowWhen(true)
            .build()
    }
}