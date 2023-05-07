package io.github.jing.work.assistant.vm

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.Operation.State.FAILURE
import androidx.work.Operation.State.SUCCESS
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.data.Address
import io.github.jing.work.assistant.data.HourMinute
import io.github.jing.work.assistant.data.ClockInInfo
import io.github.jing.work.assistant.source.SaveClockIn
import io.github.jing.work.assistant.worker.SaveClockInWorker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

class AutoClockInViewModel(app: Application) : AndroidViewModel(app) {

    private var locationManager: TencentLocationManager

    private val workAddress = MutableLiveData<Address>()

    private val workStartTime = MutableLiveData<HourMinute>()

    private val workEndTime = MutableLiveData<HourMinute>()

    private val workAddressOptions = MutableLiveData<List<Address>>()

    private val error = MutableLiveData<String>()

    private val saveResult = MediatorLiveData<Boolean>()

    private val preferences: SharedPreferences

    private val gson = GsonBuilder().create()

    private val powerManager: PowerManager

    private val app: Application

    private val composeDisable = CompositeDisposable()

    init {
        this.app = app
        locationManager = TencentLocationManager.getInstance(app)
        preferences = app.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val clockInInfoStr = preferences.getString(Constants.CLOCK_IN_INFO, "")
        if (!clockInInfoStr.isNullOrEmpty()) {
            val clockInInfo = gson.fromJson(clockInInfoStr, ClockInInfo::class.java)
            workAddress.value = clockInInfo.address
            workStartTime.value = clockInInfo.workStartTime
            workEndTime.value = clockInInfo.workEndTime
        }
        powerManager = app.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    fun setStartWorkTime(hour: Int, minute: Int) {
        workStartTime.value = HourMinute(hour, minute)
    }

    fun setOffWorkTime(hour: Int, minute: Int) {
        workEndTime.value = HourMinute(hour, minute)
    }

    fun startWorkTime(): LiveData<HourMinute> {
        return workStartTime
    }

    fun offWorkTime(): LiveData<HourMinute> {
        return workEndTime
    }

    fun getStartWorkTime(): HourMinute {
        return workStartTime.value ?: getDefaultTime()
    }

    fun getOffWorkTime(): HourMinute {
        return workEndTime.value ?: getDefaultTime()
    }

    fun getDefaultTime(): HourMinute {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return HourMinute(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    fun getWorkWorkTime(): HourMinute {
        return workEndTime.value ?: getDefaultTime()
    }

    fun setWorkAddress(address: Address) {
        workAddress.value = address
    }

    fun getWorkAddressOptions() {
        val request = TencentLocationRequest.create()
        request.locMode = TencentLocationRequest.HIGH_ACCURACY_MODE
        request.requestLevel = TencentLocationRequest.REQUEST_LEVEL_POI
        //默认允许
        request.isAllowGPS = true
        request.isAllowCache = false
        request.isAllowDirection = false
        locationManager.requestSingleFreshLocation(request, object : TencentLocationListener {
            override fun onLocationChanged(
                location: TencentLocation?,
                error: Int,
                reason: String?
            ) {
                if (TencentLocation.ERROR_OK != error) {
                    this@AutoClockInViewModel.error.value = reason
                    return
                }
                val list = mutableListOf<Address>()
                if (location != null) {
                    val detail = if (location.address.isNullOrEmpty()) {
                        "${location.province}${location.district}${location.street}"
                    } else {
                        location.address
                    }
                    list.add(Address(location.latitude, location.longitude, detail))
                    if (location.poiList != null) {
                        for (poi in location.poiList) {
                            list.add(Address(poi.latitude, poi.longitude, poi.address))
                        }
                    }
                }
                workAddressOptions.value = list
            }

            override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
                Log.i(Companion.TAG, "$name, $status, $desc")
            }

        }, Looper.getMainLooper())
    }

    fun workAddress(): LiveData<Address> {
        return workAddress
    }

    fun workAddressOptions(): LiveData<List<Address>> {
        return workAddressOptions
    }

    companion object {
        private const val TAG = "AutoClockInVm"
    }

    fun save() {
        if (workAddress.value == null) {
            error.value = "请设置工作地点"
            return
        }
        if (workStartTime.value == null) {
            error.value = "请设置上班打卡时间"
            return
        }
        if (workEndTime.value == null) {
            error.value = "请设置下班打卡时间"
            return
        }
        val clockInInfo = ClockInInfo(workAddress.value!!, workStartTime.value!!, workEndTime.value!!)
        composeDisable.add(Single.create(SaveClockIn(app, clockInInfo))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                saveResult.value = true
            }, {
                error.value = it.message
            }))
    }

    fun error(): LiveData<String> {
        return error
    }

    fun saveResult(): LiveData<Boolean> {
        return saveResult
    }
}