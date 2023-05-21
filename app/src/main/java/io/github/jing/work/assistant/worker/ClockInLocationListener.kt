package io.github.jing.work.assistant.worker

import android.content.Context
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import io.github.jing.work.assistant.Constants


/**
 * 使用Looper.getMainLooper()进行监听, ClockInLocationListener的回调是在主线程
 */
class ClockInLocationListener (
    private val context: Context,
    private val latitude: Double,
    private val longitude: Double,
    private val radius: Double,
    private val listener: OnTriggerListener) : BDAbstractLocationListener() {

    private var lastLocation: BDLocation? = null

    private val workManger: WorkManager = WorkManager.getInstance(context)

    private val broadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

//    override fun onLocationChanged(location: TencentLocation?, error: Int, reason: String?) {
//        Log.d(TAG, "onLocationChanged: $error, ${location?.latitude}, ${location?.longitude}")
//        if (TencentLocation.ERROR_OK != error || location == null) {
//            return
//        }
//        val distance = TencentLocationUtils.distanceBetween(
//            latitude,
//            longitude,
//            location.latitude,
//            location.longitude
//        )
//        val previousDistance = if (lastLocation == null) {
//            -1.0
//        } else {
//            TencentLocationUtils.distanceBetween(
//                latitude,
//                longitude,
//                lastLocation!!.latitude,
//                lastLocation!!.longitude
//            )
//        }
//        if (distance in 0.0..radius) {
//            //现在在工作地点范围内
//            if (previousDistance < 0.0) {
//                //之前没有位置, 进入工作地点范围内
//                listener.onTrigger(Constants.TRIGGER_FLAG_IN_RANGE, "进入工作地点范围内")
//            } else if (previousDistance in 0.0..radius) {
//                //之前在工作地点范围内, 停留在工作地点范围内
//                listener.onTrigger(Constants.TRIGGER_FLAG_STAY_IN_RANGE, "停留在工作地点范围内")
//            } else {
//                //之前在工作地点范围外, 进入工作地点范围
//                listener.onTrigger(Constants.TRIGGER_FLAG_IN_RANGE, "进入工作地点范围内")
//            }
//        } else {
//            //现在在工作地点范围外
//            if (previousDistance < 0.0) {
//                //之前没有位置, 进入工作地点范围之外
//                listener.onTrigger(Constants.TRIGGER_FLAG_OUT_OF_RANGE, "进入工作地点范围外")
//            } else if (previousDistance in 0.0..radius) {
//                //之前在工作范围内, 进入工作地点范围之外
//                listener.onTrigger(Constants.TRIGGER_FLAG_OUT_OF_RANGE, "进入工作地点范围外")
//            } else {
//                //之前在工作范围外, 停留在工作地点范围外
//                listener.onTrigger(Constants.TRIGGER_FLAG_STAY_OUT_OF_RANGE, "进入工作地点范围外")
//            }
//        }
//        lastLocation = location
//    }
//
//    override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
//        Log.d(TAG, "$name, $status, $desc")
//    }

    companion object {
        const val TAG = "ClockInLocationListener"
    }

    interface OnTriggerListener {
        fun onTrigger(flag: Int, text: String)
    }

    override fun onReceiveLocation(location: BDLocation?) {

    }
}