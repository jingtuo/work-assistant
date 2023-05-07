package io.github.jing.work.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.worker.ClockInWorker

class ClockInReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive ")
        if (ACTION_CLOCK_IN == intent.action) {
            //自动打卡
            val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
            val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
            val address = intent.getStringExtra(Constants.ADDRESS)
            val autoClockInFlag = intent.getStringExtra(Constants.AUTO_CLOCK_IN_FLAG)
            val flags = if (Constants.WORK_START_CLOCK_IN == autoClockInFlag) {
                Constants.TRIGGER_FLAG_IN_RANGE and Constants.TRIGGER_FLAG_STAY_IN_RANGE
            } else {
                Constants.TRIGGER_FLAG_ALL
            }
            val workManager = WorkManager.getInstance(context)
            val data = createData(latitude, longitude, address?:"", flags)
            val worker = OneTimeWorkRequestBuilder<ClockInWorker>()
                .setInputData(data)
                .build()
            workManager.enqueueUniqueWork(autoClockInFlag?:Constants.WORK_START_CLOCK_IN,
                ExistingWorkPolicy.REPLACE,
                worker
            )
        }
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

    companion object {
        const val TAG = "AutoClockInReceiver"
        const val ACTION_CLOCK_IN = "io.github.jing.work.assistant.ACTION_CLOCK_IN"
    }
}