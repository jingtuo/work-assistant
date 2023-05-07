package io.github.jing.work.assistant.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.data.ClockInInfo
import io.github.jing.work.assistant.receiver.ClockInReceiver
import java.time.Duration
import java.util.Calendar

/**
 * 保存打卡信息
 */
class SaveClockInWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val clockInInfoStr: String

    private val gson: Gson = GsonBuilder().create()

    private val workManager: WorkManager

    private val alarmManager: AlarmManager

    init {
        clockInInfoStr = params.inputData.getString(Constants.CLOCK_IN_INFO) ?: ""
        workManager = WorkManager.getInstance(appContext)
        alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override suspend fun doWork(): Result {
        val preferences =
            applicationContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        if (clockInInfoStr.isNullOrEmpty()) {
            return Result.failure()
        }
        preferences.edit().putString(Constants.CLOCK_IN_INFO, clockInInfoStr).apply()
        val clockInInfo = gson.fromJson(clockInInfoStr, ClockInInfo::class.java)
        //计算开始
        val latitude = clockInInfo.address.latitude
        val longitude = clockInInfo.address.longitude
        val workStartHour = clockInInfo.workStartTime.hour
        val workStartMinute = clockInInfo.workStartTime.minute
        val workEndHour = clockInInfo.workEndTime.hour
        val workEndMinute = clockInInfo.workEndTime.minute
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val curHour = calendar.get(Calendar.HOUR_OF_DAY)
        val curMinute = calendar.get(Calendar.MINUTE)
        calendar.add(Calendar.MINUTE, 20)

        //清除秒和毫秒
//        calendar.set(Calendar.SECOND, 0)
//        calendar.set(Calendar.MILLISECOND, 0)
//        //上班打卡任务
//        var delayMinutes = calculateMinutes(workStartHour, workStartMinute, curHour, curMinute)
//        if (delayMinutes < 0) {
//            //工作开始时间已过, 延期到明天
//            delayMinutes += 24 * 60
//        }
//        calendar.add(Calendar.MINUTE, delayMinutes)
//        var data = createData(
//            latitude, longitude, clockInInfo.address.detail,
//            Constants.TRIGGER_FLAG_IN_RANGE and Constants.TRIGGER_FLAG_STAY_IN_RANGE
//        )
//        val startWorker = PeriodicWorkRequestBuilder<WorkClockInWorker>(
//            Duration.ofHours(24),
//            Duration.ofMinutes(Constants.CLOCK_IN_FLEX_MINUTE_5.toLong())
//        )
//            .setInputData(data)
//            .setInitialDelay(Duration.ofMinutes(delayMinutes.toLong()))
//            .build()

        val intent = Intent(ClockInReceiver.ACTION_CLOCK_IN)
        intent.setPackage(applicationContext.packageName)
        intent.putExtra(Constants.LATITUDE, latitude)
        intent.putExtra(Constants.LONGITUDE, longitude)
        intent.putExtra(Constants.ADDRESS, clockInInfo.address.detail)
        intent.putExtra(Constants.AUTO_CLOCK_IN_FLAG, Constants.WORK_START_CLOCK_IN)
        val pIntent = PendingIntent.getBroadcast(applicationContext, Ids.getPendingIntentRequestCode(), intent,
            PendingIntent.FLAG_IMMUTABLE xor PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pIntent)

//        workManager.enqueueUniquePeriodicWork(
//            Constants.WORK_START_CLOCK_IN,
//            ExistingPeriodicWorkPolicy.REPLACE,
//            startWorker
//        )
        //下班打卡任务, 此处增加5分钟, 下班之后5分钟内打卡
        var delayMinutes = calculateMinutes(
            workEndHour,
            workEndMinute + Constants.CLOCK_IN_FLEX_MINUTE_5,
            curHour,
            curMinute
        )
        if (delayMinutes < 0) {
            //工作结束时间已过, 延期到明天
            delayMinutes += 24 * 60
        }
        val data = createData(
            latitude, longitude, clockInInfo.address.detail, Constants.TRIGGER_FLAG_ALL
        )
        val endWorker = PeriodicWorkRequestBuilder<ClockInWorker>(
            Duration.ofHours(24),
            Duration.ofMinutes(Constants.CLOCK_IN_FLEX_MINUTE_5.toLong())
        )
            .setInputData(data)
            .setInitialDelay(Duration.ofMinutes(delayMinutes.toLong()))
            .build()
        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_END_CLOCK_IN,
            ExistingPeriodicWorkPolicy.REPLACE,
            endWorker
        )
        return Result.success()
    }

    private fun createData(
        latitude: Double,
        longitude: Double,
        address: String,
        triggerFlags: Int
    ): Data {
        return Data.Builder()
            .putDouble(Constants.LATITUDE, latitude)
            .putDouble(Constants.LONGITUDE, longitude)
            .putString(Constants.ADDRESS, address)
            .putInt(Constants.TRIGGER_FLAGS, triggerFlags)
            .build()
    }

    private fun calculateMinutes(
        hourLeft: Int,
        minuteLeft: Int,
        hourRight: Int,
        minuteRight: Int
    ): Int {
        return (hourLeft - hourRight) * 60 + minuteLeft - minuteRight
    }

    companion object {
        const val TAG = "SaveClockInWorker"
    }
}