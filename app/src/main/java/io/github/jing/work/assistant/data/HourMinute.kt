package io.github.jing.work.assistant.data

import androidx.annotation.Keep

@Keep
data class HourMinute(val hour: Int, val minute: Int) {
    override fun toString(): String {
        return String.format("%02d:%02d", hour, minute)
    }
}
