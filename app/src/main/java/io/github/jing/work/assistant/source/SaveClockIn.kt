package io.github.jing.work.assistant.source

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.gson.GsonBuilder
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.data.ClockInInfo
import io.github.jing.work.assistant.data.toData
import io.github.jing.work.assistant.worker.ClockInWorker
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.core.SingleOnSubscribe


/**
 * 保存打卡信息
 */
class SaveClockIn(private val context: Context, private val clockInInfo: ClockInInfo): SingleOnSubscribe<String> {

    override fun subscribe(emitter: SingleEmitter<String>) {
        val preferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        preferences.edit().putString(Constants.CLOCK_IN_INFO, gson.toJson(clockInInfo)).apply()
        //启动长期运行的Work
        val workManager = WorkManager.getInstance(context)
        val data = clockInInfo.toData()
        val request = OneTimeWorkRequestBuilder<ClockInWorker>()
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.beginUniqueWork(Constants.UNIQUE_WORK_CLOCK_IN, ExistingWorkPolicy.REPLACE, request)
            .enqueue()
        emitter.onSuccess("保存成功")
    }
}