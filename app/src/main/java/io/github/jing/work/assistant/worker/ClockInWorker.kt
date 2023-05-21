package io.github.jing.work.assistant.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.ext.createChannel
import io.github.jing.work.assistant.ext.showNotification
import io.github.jing.work.assistant.openQW
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.abs
import kotlin.time.Duration.Companion.minutes

/**
 * 上下班打卡任务,
 * 长期运行的后台任务
 *
 */
class ClockInWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), ClockInLocationListener.OnTriggerListener {

    private val notificationManager: NotificationManagerCompat
    private val latitude: Double
    private val longitude: Double
    private val address: String
    private val radius: Double
    private val workStartHour: Int
    private val workStartMinute: Int
    private val workEndHour: Int
    private val workEndMinute: Int

    private val triggerFlags: Int
    private val duration: Int
    private val powerManager: PowerManager

    private val locationListener: ClockInLocationListener

    init {
        notificationManager = NotificationManagerCompat.from(appContext)
        latitude = params.inputData.getDouble(Constants.LATITUDE, 0.0)
        longitude = params.inputData.getDouble(Constants.LONGITUDE, 0.0)
        radius = params.inputData.getDouble(Constants.RADIUS, Constants.GEOFENCE_RADIUS_50)
        address = params.inputData.getString(Constants.ADDRESS) ?: ""
        //默认9:00上班
        workStartHour = params.inputData.getInt(Constants.WORK_START_HOUR, 9)
        workStartMinute = params.inputData.getInt(Constants.WORK_START_MINUTE, 0)
        //默认17:30下班
        workEndHour = params.inputData.getInt(Constants.WORK_END_HOUR, 17)
        workEndMinute = params.inputData.getInt(Constants.WORK_END_MINUTE, 30)
        triggerFlags = params.inputData.getInt(Constants.TRIGGER_FLAGS, Constants.TRIGGER_FLAG_NONE)
        duration =
            params.inputData.getInt(Constants.DURATION, Constants.CLOCK_IN_WORKER_DURATION_DEFAULT)
        powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        locationListener =
            ClockInLocationListener(applicationContext, latitude, longitude, radius, this)
    }


    override suspend fun doWork(): Result {
        val wakeLockTag = "${applicationContext.packageName}.CLOCK_IN"
//        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLockTag)
        try {
//            setForeground(createForeground())
//            wakeLock.acquire(10000)
//            if (wakeLock.isHeld) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                var curHour = calendar.get(Calendar.HOUR_OF_DAY)
                var curMinute = calendar.get(Calendar.MINUTE)
                var toStartMinutes = 0
                var toEndMinutes = 0
                while (curHour <= Int.MAX_VALUE && curMinute <= Int.MAX_VALUE) {
                    Log.i(
                        TAG,
                        "$curHour:$curMinute, $workStartHour:$workStartMinute - $workEndHour:$workEndMinute"
                    )
                    toStartMinutes =
                        calculateMinutes(curHour, curMinute, workStartHour, workStartMinute)
                    //上班打卡, 提前5分钟
                    toEndMinutes = calculateMinutes(curHour, curMinute, workEndHour, workEndMinute)
                    //特殊场景处理, 下班5分钟内, 打下班卡
                    if (toEndMinutes in -5..0) {
                        clockIn()
                        if (toEndMinutes == 0) {
                            delay(1.minutes)
                        } else {
                            delay(abs(toEndMinutes).minutes)
                        }
                        release()
                        calendar.timeInMillis = System.currentTimeMillis()
                        curHour = calendar.get(Calendar.HOUR_OF_DAY)
                        curMinute = calendar.get(Calendar.MINUTE)
                        continue
                    }
                    //矫正距离下次上班打卡和下班打卡的分钟数
                    if (toStartMinutes < 0) {
                        //上班打卡时间已过
                        toStartMinutes += Constants.MINUTES_OF_DAY
                    }
                    if (toEndMinutes < 0) {
                        //下班打卡时间已过
                        toEndMinutes += Constants.MINUTES_OF_DAY
                    }
                    //选择分钟数少的进行打卡
                    if (toStartMinutes <= toEndMinutes) {
                        //上班打卡
                        if (toStartMinutes in 0..5) {
                            clockIn()
                            if (toStartMinutes == 0) {
                                delay(1.minutes)
                            } else {
                                delay(toStartMinutes.minutes)
                            }
                            release()
                        } else {
                            delay((toStartMinutes - 5).minutes)
                        }
                    } else {
                        //下班打卡
                        delay(toEndMinutes.minutes)
                    }
                    calendar.timeInMillis = System.currentTimeMillis()
                    curHour = calendar.get(Calendar.HOUR_OF_DAY)
                    curMinute = calendar.get(Calendar.MINUTE)
                }
//            }
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            return Result.failure(Data.Builder().putString("error", e.message).build())
        } finally {
//            wakeLock.release()
        }
    }

    private fun createForeground(): ForegroundInfo {
        return ForegroundInfo(
            Ids.getNotificationId(),
            createNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
    }

    private fun createNotification(): Notification {
        applicationContext.createChannel()
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID_AUTO_CLOCK_IN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(applicationContext.getString(R.string.auto_clock_in))
            .setContentText(applicationContext.getText(R.string.running))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setShowWhen(true)
            .build()
    }

    private fun calculateMinutes(fromHour: Int, fromMinute: Int, toHour: Int, toMinute: Int): Int {
        return (toHour - fromHour) * 60 + toMinute - fromMinute
    }

    companion object {
        const val TAG = "WorkClockInWorker"
    }

    private fun clockIn() {
//        val request = TencentLocationRequest.create()
//        request.locMode = TencentLocationRequest.HIGH_ACCURACY_MODE
//        request.isAllowCache = false
//        request.isAllowGPS = true
//        request.isAllowDirection = false
//        request.requestLevel = TencentLocationRequest.REQUEST_LEVEL_GEO
//        //每15秒获取
//        request.interval = 15000
//        locationManager.requestLocationUpdates(request, locationListener, Looper.getMainLooper())
    }

    private fun release() {
//        locationManager.removeUpdates(locationListener)
    }

    override fun onTrigger(flag: Int, text: String) {
        Log.d(TAG, "trigger: $text")
        release()
        //显示通知
        applicationContext.showNotification(createNotification(applicationContext, text))
        openQW(applicationContext)
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
}