package io.github.jing.work.assistant

/**
 * 用于管理id
 */
object Ids {
    private var notificationId = 1;

    private var pIntentRequestCode = 1;

    fun getNotificationId(): Int {
        return notificationId++
    }


    fun getPendingIntentRequestCode(): Int {
        return pIntentRequestCode++
    }
}