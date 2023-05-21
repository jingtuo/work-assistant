package io.github.jing.work.assistant.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.baidu.geofence.GeoFence
import com.baidu.geofence.GeoFenceClient
import com.baidu.geofence.GeoFenceListener
import com.baidu.geofence.model.DPoint
import com.google.gson.GsonBuilder
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.MainActivity
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.data.ClockInInfo
import io.github.jing.work.assistant.ext.ACTION_REPEAT_EXACT_ALARM
import io.github.jing.work.assistant.ext.EXTRA_TRIGGER_AT_MILLS
import io.github.jing.work.assistant.ext.createChannel
import io.github.jing.work.assistant.ext.setRepeatExactAlarm
import io.github.jing.work.assistant.receiver.AlarmReceiver
import io.github.jing.work.assistant.receiver.GeofenceReceiver

class ClockInService: Service() {

    private var notificationId: Int? = null

    private var wakeLock: WakeLock? = null

    private var geoFenceClient: GeoFenceClient? = null

    private var geofenceReceiver: GeofenceReceiver? = null

    private var geoFence: GeoFence? = null

    private var alarmReceiver: AlarmReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "$this onStartCommand: $intent")
        if (notificationId == null) {
            notificationId = Ids.getNotificationId()
        }
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLockTag = "$packageName.CLOCK_IN"
        startForeground(notificationId!!, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        if (wakeLock != null) {
            wakeLock!!.release()
        }
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLockTag)
        wakeLock!!.acquire(10000)
        if (wakeLock!!.isHeld) {
            geoFenceClient = GeoFenceClient(this.applicationContext)
            geoFenceClient!!.setActivateAction(GeoFenceClient.GEOFENCE_IN_OUT_STAYED)
            geoFenceClient!!.setTriggerCount(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
            //单位是秒
            geoFenceClient!!.setStayTime(60)
            val preferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
            val gson = GsonBuilder().create()
            val clockInInfo = gson.fromJson(preferences.getString(Constants.CLOCK_IN_INFO, ""), ClockInInfo::class.java)

            if (geoFence != null) {
                //移除之前的地理围栏
                geoFenceClient!!.removeGeoFence(geoFence!!)
            }
            geoFenceClient!!.setGeoFenceListener(object: GeoFenceListener {
                override fun onGeoFenceCreateFinished(
                    list: MutableList<GeoFence>?,
                    errCode: Int,
                    errMsg: String?
                ) {
                    if (GeoFence.ADDGEOFENCE_SUCCESS == errCode) {
                        if ((list?.size ?: 0) >= 1) {
                            geoFence = list!![0]
                        }
                    }
                }

            })
            geoFenceClient!!.addGeoFence(DPoint(clockInInfo.address.latitude, clockInInfo.address.longitude),
                clockInInfo.address.coordinateType, 30F, "上班打卡")
            val bIntent = Intent(GeofenceReceiver.ACTION_GEOFENCE).apply {
                setPackage(packageName)
                putExtra(Constants.LATITUDE, clockInInfo.address.latitude)
                putExtra(Constants.LONGITUDE, clockInInfo.address.longitude)
            }
            geoFenceClient!!.createPendingIntent(GeofenceReceiver.ACTION_GEOFENCE)
//            geofenceManager!!.addFence(geofence, pIntent)
//            val workManager = WorkManager.getInstance(this)
//            val data = clockInInfo.toData()
//            val request = OneTimeWorkRequestBuilder<ClockInWorker>().setInputData(data)
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .build()
//            workManager.beginUniqueWork(Constants.UNIQUE_WORK_CLOCK_IN, ExistingWorkPolicy.REPLACE, request)
//                    .enqueue()
            val extras = Bundle().apply {
                putLong(EXTRA_TRIGGER_AT_MILLS, System.currentTimeMillis() + 10 * 60000)
            }
            setRepeatExactAlarm(extras)
            var filter: IntentFilter? = null
            if (geofenceReceiver == null) {
                geofenceReceiver = GeofenceReceiver()
                filter = IntentFilter().apply {
                    addAction(GeofenceReceiver.ACTION_GEOFENCE)
                }
                registerReceiver(geofenceReceiver, filter)
            }
            if (alarmReceiver == null) {
                alarmReceiver = AlarmReceiver()
                filter = IntentFilter().apply {
                    addAction(ACTION_REPEAT_EXACT_ALARM)
                }
                registerReceiver(alarmReceiver, filter)
            }
        }
        //当前targetSdkVersion=33, 父类使用START_STICKY
        //START_STICKY: 服务被系统杀死以后, 系统重建服务时, intent=null
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        applicationContext.createChannel()
        //设置Category=CATEGORY_NAVIGATION是为了立即显示通知
        //设置setForegroundServiceBehavior是为了立即显示通知
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID_AUTO_CLOCK_IN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(applicationContext.getString(R.string.auto_clock_in))
            .setContentText(applicationContext.getText(R.string.running))
            .setContentIntent(createContentIntent())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .setShowWhen(true)
            .build()
    }

    private fun createContentIntent(): PendingIntent{
        val intent = Intent(this, MainActivity::class.java)
        intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, Ids.getPendingIntentRequestCode(), intent,
            PendingIntent.FLAG_IMMUTABLE xor PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        geoFenceClient?.removeGeoFence()
        wakeLock?.release()
        geofenceReceiver?.let {
            unregisterReceiver(it)
        }
        alarmReceiver?.let {
            unregisterReceiver(it)
        }
    }

    companion object {
        const val ACTION_CLOSE_CLOCK_IN = "io.github.jing.work.assistant.CLOSE_CLOCK_IN"
        const val TAG = "ClockInService"
    }
}