package io.github.jing.work.assistant.data

import androidx.annotation.Keep

@Keep
data class Address(val latitude: Double, val longitude: Double, val detail: String)
