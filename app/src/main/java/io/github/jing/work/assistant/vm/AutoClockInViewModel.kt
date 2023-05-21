package io.github.jing.work.assistant.vm

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.GsonBuilder
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.data.Address
import io.github.jing.work.assistant.data.HourMinute
import io.github.jing.work.assistant.data.ClockInInfo
import io.github.jing.work.assistant.ext.setAlarm
import io.github.jing.work.assistant.location.SingleLocationListener
import io.github.jing.work.assistant.service.ClockInService
import io.github.jing.work.assistant.source.SaveClockIn
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

class AutoClockInViewModel(app: Application) : AndroidViewModel(app) {

    private var locationClient: LocationClient? = null

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

    private var locationListener: SingleLocationListener? = null

    init {
        this.app = app
        initLocation()
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

    private fun initLocation() {
        try {
            val option = LocationClientOption()
            //定位模式, 高精度
            option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
            //setCoorType: 坐标类型
            //GCJ02：国测局坐标, 默认
            //BD09ll：百度经纬度坐标；
            //BD09：百度墨卡托坐标；
            //首次定位设置
            option.setOnceLocation(true)
            option.setFirstLocType(LocationClientOption.FirstLocType.ACCURACY_IN_FIRST_LOC)
            //定位时间间隔, 单位为毫秒, 最小值1000ms, 设置为0, 表示请求一次
            //经测试发现, 首次定位的结果不准确
            option.setScanSpan(0)
            //设置使用卫星定位, 默认false
            option.isOpenGnss = false
            //设置是否当卫星定位有效时按照1S/1次频率输出卫星定位结果，默认false
            option.isLocationNotify = false
            //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
            option.setIgnoreKillProcess(true)
            //设置是否收集Crash信息，默认收集，即参数为false
            option.isIgnoreCacheException = true
            //设置是否需要过滤卫星定位仿真结果，默认需要，即参数为false
            option.setEnableSimulateGnss(false)
            //需要当前位置的地址信息
            option.setIsNeedAddress(true)
            //需要最新版本
            option.setNeedNewVersionRgc(true)
            //需要周边POI
            option.setIsNeedLocationPoiList(true)
            //需要语义化信息
            option.setIsNeedLocationDescribe(true)
            //北斗定位
            option.isEnableBeidouMode = false
            option.disableLocCache = true
            locationClient = LocationClient(app.applicationContext)
            locationListener = object : SingleLocationListener(locationClient!!) {

                override fun onReceiveLocationV2(location: BDLocation) {
                    Log.i(
                        TAG,
                        "location: ${location.latitude}, ${location.longitude}, ${location.addrStr}, " +
                                "${location.address}, ${location.floor}"
                    )
                    val detail = if (location.addrStr.isNullOrEmpty()) {
                        "当前地址"
                    } else {
                        location.addrStr
                    }
                    location.poiList?.forEach { item ->
                        Log.i(TAG, "poi: " + item.name + ", " + item.addr)
                    }
                    workAddress.value =
                        Address(location.coorType, location.latitude, location.longitude, detail)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
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
        locationClient!!.registerLocationListener(locationListener)
        locationClient!!.start()
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
        val clockInInfo =
            ClockInInfo(workAddress.value!!, workStartTime.value!!, workEndTime.value!!)
        composeDisable.add(Single.create(SaveClockIn(app, clockInInfo))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                app.startForegroundService(Intent(app, ClockInService::class.java))
                saveResult.value = true
            }, {
                error.value = it.message
            })
        )
    }

    fun error(): LiveData<String> {
        return error
    }

    fun saveResult(): LiveData<Boolean> {
        return saveResult
    }

    override fun onCleared() {
        super.onCleared()
    }
}