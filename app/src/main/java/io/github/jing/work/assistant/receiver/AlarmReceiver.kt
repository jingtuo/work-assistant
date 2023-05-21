package io.github.jing.work.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "${intent.action} arrived")
    }

    companion object {
        const val TAG = "AlarmReceiver"
    }
}