package io.github.jing.work.assistant.data

import androidx.annotation.Keep

@Keep
data class ClockInInfo(val address: Address, val workStartTime: HourMinute, val  workEndTime: HourMinute)
