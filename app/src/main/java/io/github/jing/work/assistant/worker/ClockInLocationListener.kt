package io.github.jing.work.assistant.worker

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationUtils
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.openQW
import io.github.jing.work.assistant.receiver.GeofenceReceiver
import java.util.UUID


/**
 * 使用Looper.getMainLooper()进行监听, ClockInLocationListener的回调是在主线程
 */
class ClockInLocationListener(
    private val context: Context,
    private val workerId: UUID,
    private val latitude: Double,
    private val longitude: Double,
    private val radius: Double,
    private val triggerFlags: Int
) : TencentLocationListener {

    private var lastLocation: TencentLocation? = null

    private val locationManager: TencentLocationManager = TencentLocationManager.getInstance(context)

    private val workManger: WorkManager = WorkManager.getInstance(context)

    private val broadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onLocationChanged(location: TencentLocation?, error: Int, reason: String?) {
        if (TencentLocation.ERROR_OK != error || location == null) {
            return
        }
        val distance = TencentLocationUtils.distanceBetween(
            latitude,
            longitude,
            location.latitude,
            location.longitude
        )
        val previousDistance = if (lastLocation == null) {
            -1.0
        } else {
            TencentLocationUtils.distanceBetween(
                latitude,
                longitude,
                lastLocation!!.latitude,
                lastLocation!!.longitude
            )
        }
        if (distance in 0.0..radius) {
            //现在在工作地点范围内
            if (previousDistance < 0.0) {
                //之前没有位置, 进入工作地点范围内
                if ((Constants.TRIGGER_FLAG_IN_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_IN_RANGE) {
                    trigger("进入工作地点范围内")
                }
            } else if (previousDistance in 0.0..radius) {
                //之前在工作地点范围内, 停留在工作地点范围内
                if ((Constants.TRIGGER_FLAG_STAY_IN_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_STAY_IN_RANGE) {
                    trigger("停留在工作地点范围内")
                }
            } else {
                //之前在工作地点范围外, 进入工作地点范围
                if ((Constants.TRIGGER_FLAG_IN_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_IN_RANGE) {
                    trigger("进入工作地点范围内")
                }
            }
        } else {
            //现在在工作地点范围外
            if (previousDistance < 0.0) {
                //之前没有位置, 进入工作地点范围之外
                if ((Constants.TRIGGER_FLAG_OUT_OF_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_OUT_OF_RANGE) {
                    trigger("进入工作地点范围外")
                }
            } else if (previousDistance in 0.0..radius) {
                //之前在工作范围内, 进入工作地点范围之外
                if ((Constants.TRIGGER_FLAG_OUT_OF_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_OUT_OF_RANGE) {
                    trigger("进入工作地点范围外")
                }
            } else {
                //之前在工作范围外, 停留在工作地点范围外
                if ((Constants.TRIGGER_FLAG_STAY_OUT_OF_RANGE and triggerFlags) == Constants.TRIGGER_FLAG_STAY_OUT_OF_RANGE) {
                    trigger("停留在工作地点范围外")
                }
            }
        }
        lastLocation = location
    }

    private fun trigger(text: String) {
        //取消定位监听
        locationManager.removeUpdates(this)
        //取消当前任务
        workManger.cancelWorkById(workerId)
        //sendBroadcast未生效
        //显示通知
        context.showNotification(createNotification(context, text))
        openQW(context)
    }

    override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
        Log.d(TAG, "$name, $status, $desc")
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
        const val TAG = "ClockInLocationListener"
    }
}