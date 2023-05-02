# 工作助手

## 技术选型

1. 使用Kotlin开发语言, 属性Kotlin特性
2. 使用Xml创建定义布局, 因为Compose的许多组件还处于试验阶段, 一定程序上影响开发效率, 后续部分组件使用Compose
3. 使用移动安全联盟的OAID, 需要营业执照, 所以选择UUID记录一个deviceId
4. 定位SDK选择腾讯地图, 考虑后续在微信小程序里面使用定位功能


## 版本

### 0.0.1

1. 基于ActivityTaskManager的日志, 找到企微的包名以及入口页面
    ```text
    START u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10200000 cmp=com.tencent.wework/.launch.LaunchSplashActivity bnds=[53,898][305,1255] mCallingUid=10077} from uid 10077
    ```

2. 使用Intent启动企微
    ```kotlin
    private fun startQW() {
        val intent = Intent()
        val component = ComponentName("com.tencent.wework", "com.tencent.wework.launch.LaunchSplashActivity")
        intent.component = component
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    ```
3. 使用WorkManager添加定时任务启动企微
4. 为了减少对设备的电量消耗, 设计成在上下班打卡时间的前后5分钟内进行打卡, 但是腾讯地图的地理围栏是在用户进出地理围栏才触发, 用户可能不在设定5分钟内进出地理围栏, 最终设计成在5分钟开启持续定位功能, 只要发现在办公地点附近就打开企微



## 参考资料

- [Compose 文档](https://developer.android.google.cn/jetpack/compose/documentation?hl=zh-cn)
- [WorkManager 概览](https://developer.android.google.cn/topic/libraries/architecture/workmanager?hl=zh-cn)
- [Background Work with WorkManager](https://developer.android.google.cn/codelabs/basic-android-kotlin-compose-workmanager?continue=https%3A%2F%2Fdeveloper.android.google.cn%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-7-pathway-1%3Fhl%3Den%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-workmanager&hl=en#0)
- [Material Design 3](https://developer.android.google.cn/jetpack/compose/designsystems/material3)
- [Material Design 3 Component](https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/package-summary)
- [腾讯地图 API](https://lbs.qq.com/mobile/androidLocationSDK/androidGeoGuide/androidGeoContinue)
    - 百度地图API的地理围栏更新时间过旧(20220708)
    - 高德地图API跟百度地图API十分相似
- [调度精准闹钟](https://developer.android.google.cn/about/versions/14/changes/schedule-exact-alarms)
- [MDC-Android](https://github.com/material-components/material-components-android)
- [OkHttp](https://github.com/square/okhttp)

## 课外知识

1. gradle 8.0要求的jdk 17
3. Material Theme Builder可以创建M3的主题, 但是国内无法访问, 求镜像资源