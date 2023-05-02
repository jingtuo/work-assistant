package io.github.jing.work.assistant.worker

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * 上下班打卡任务,
 */
class WorkClockInWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val locationManager: TencentLocationManager
    private val notificationManager: NotificationManagerCompat
    private val latitude: Double
    private val longitude: Double
    private val address: String
    private val radius: Double
    private val triggerFlags: Int
    private val duration: Int

    init {
        locationManager = TencentLocationManager.getInstance(appContext)
        notificationManager = NotificationManagerCompat.from(appContext)
        latitude = params.inputData.getDouble(Constants.LATITUDE, 0.0)
        longitude = params.inputData.getDouble(Constants.LONGITUDE, 0.0)
        radius = params.inputData.getDouble(Constants.RADIUS, Constants.GEOFENCE_RADIUS_50)
        address = params.inputData.getString(Constants.ADDRESS) ?: ""
        triggerFlags = params.inputData.getInt(Constants.TRIGGER_FLAGS, Constants.TRIGGER_FLAG_NONE)
        duration = params.inputData.getInt(Constants.DURATION, Constants.CLOCK_IN_WORKER_DURATION_DEFAULT)
    }


    override suspend fun doWork(): Result {
        try {
            setForeground(createForeground())
            val request = TencentLocationRequest.create()
            request.locMode = TencentLocationRequest.HIGH_ACCURACY_MODE
            request.isAllowCache = false
            request.isAllowGPS = true
            request.isAllowDirection = false
            request.requestLevel = TencentLocationRequest.REQUEST_LEVEL_GEO
            //每10秒获取
            request.interval = 10000
            locationManager.requestLocationUpdates(
                request,
                ClockInLocationListener(applicationContext, id, latitude, longitude, radius, triggerFlags),
                Looper.getMainLooper()
            )
            //强制等待, 直到定位满足触发条件
            delay(duration.minutes)
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            return Result.failure(Data.Builder().putString("error", e.message).build())
        }
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

    companion object {
        const val TAG = "WorkClockInWorker"
    }
}