package io.github.jing.work.assistant

import android.content.ComponentName
import android.content.Context
import android.content.Intent

fun openQW(context: Context) {
    val intent = Intent()
    val component =
        ComponentName("com.tencent.wework", "com.tencent.wework.launch.LaunchSplashActivity")
    intent.component = component
    intent.action = Intent.ACTION_MAIN
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}