package io.github.jing.work.assistant.location

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient

abstract class SingleLocationListener(private val client: LocationClient): BDAbstractLocationListener() {

    final override fun onReceiveLocation(location: BDLocation?) {
        if (location != null) {
            onReceiveLocationV2(location)
        }
        client.unRegisterLocationListener(this)
        client.stop()
    }

    abstract fun onReceiveLocationV2(location: BDLocation)

}