package io.github.jing.work.assistant.data

import androidx.annotation.Keep
import androidx.work.Data
import io.github.jing.work.assistant.Constants

@Keep
data class ClockInInfo(val address: Address, val workStartTime: HourMinute, val  workEndTime: HourMinute)

fun ClockInInfo.toData(): Data {
    return Data.Builder().putDouble(Constants.LATITUDE, address.latitude)
        .putDouble(Constants.LONGITUDE, address.longitude)
        .putString(Constants.ADDRESS, address.detail)
        .putInt(Constants.WORK_START_HOUR, workStartTime.hour)
        .putInt(Constants.WORK_START_MINUTE, workStartTime.minute)
        .putInt(Constants.WORK_END_HOUR, workEndTime.hour)
        .putInt(Constants.WORK_END_MINUTE, workEndTime.minute)
        .build()
}
