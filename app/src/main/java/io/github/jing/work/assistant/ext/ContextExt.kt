package io.github.jing.work.assistant.ext

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.Ids
import io.github.jing.work.assistant.R
import java.util.Calendar

const val ACTION_REPEAT_EXACT_ALARM = "io.github.jing.work.assistant.REPEAT_EXACT_ALARM"
const val ACTION_ALARM_CLOCK = "io.github.jing.work.assistant.ALARM_CLOCK"
const val EXTRA_TRIGGER_AT_MILLS = "TRIGGER_AT_MILLS"

/**
 * 设置重复闹钟, 必须传入参数:
 * - [EXTRA_TRIGGER_AT_MILLS]
 */
fun Context.setRepeatExactAlarm(extras: Bundle) {
    if (!extras.containsKey(EXTRA_TRIGGER_AT_MILLS)) {
        return
    }
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(ACTION_REPEAT_EXACT_ALARM).apply {
        setPackage(packageName)
        putExtras(extras)
    }
    val pIntent = PendingIntent.getBroadcast(
        this, Ids.getPendingIntentRequestCode(), intent,
        PendingIntent.FLAG_MUTABLE xor PendingIntent.FLAG_UPDATE_CURRENT
    )
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        extras.getLong(EXTRA_TRIGGER_AT_MILLS),
        pIntent
    )
}


/**
 * 设置一个闹钟
 * OPPO Reno 3元气版, 不论skipUi设置成什么, 都会跳到闹钟应用
 */
fun Context.setAlarm(
    message: String, hour: Int, minute: Int,
    days: ArrayList<Int> = arrayListOf(),
    ringtone: Boolean = false,
    vibrate: Boolean = false,
    skipUi: Boolean = true
) {
    val extras = createSetAlarmExtras(message, hour, minute, days, ringtone, vibrate, skipUi)
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtras(extras)
        flags += Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

fun createSetAlarmExtras(message: String, hour: Int, minute: Int,
                         days: ArrayList<Int> = arrayListOf(
                             Calendar.MONDAY,
                             Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY
                         ),
                         ringtone: Boolean = false,
                         vibrate: Boolean = false,
                         skipUi: Boolean = true): Bundle {
    return Bundle().apply {
        putString(AlarmClock.EXTRA_MESSAGE, message)
        putInt(AlarmClock.EXTRA_HOUR, hour)
        putInt(AlarmClock.EXTRA_MINUTES, minute)
        putIntegerArrayList(AlarmClock.EXTRA_DAYS, days)
        putBoolean(AlarmClock.EXTRA_RINGTONE, ringtone)
        putBoolean(AlarmClock.EXTRA_VIBRATE, vibrate)
        putBoolean(AlarmClock.EXTRA_SKIP_UI, skipUi)
    }
}

fun Context.getNextAlarmClock(): AlarmClockInfo {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return alarmManager.nextAlarmClock
}


fun Context.createChannel() {
    val notificationManager = NotificationManagerCompat.from(this)
    var channel =
        notificationManager.getNotificationChannelCompat(Constants.CHANNEL_ID_AUTO_CLOCK_IN)
    if (channel == null) {
        channel = NotificationChannelCompat.Builder(
            Constants.CHANNEL_ID_AUTO_CLOCK_IN,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(applicationContext.getString(R.string.auto_clock_in))
            .build()
        notificationManager.createNotificationChannel(channel)
    }
}

@SuppressLint("MissingPermission")
fun Context.showNotification(notification: Notification) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.notify(Ids.getNotificationId(), notification)
}