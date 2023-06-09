# 工作助手

## 技术选型

1. 使用Kotlin开发语言, 熟悉Kotlin特性
2. 使用Xml创建定义布局, 因为Compose的许多组件还处于试验阶段, 一定程序上影响开发效率, 后续部分组件使用Compose
3. 使用移动安全联盟的OAID, 需要营业执照, 所以选择UUID记录一个deviceId
4. 定位SDK选择腾讯地图, 考虑后续在微信小程序里面使用定位功能

### 兼容问题

| 类 | 属性/方法 | OPPO Reno 3 元气版 | 
| :-- | :-- | :-- |
| AlarmManager | setInexactRepeating | 应用切后台锁屏, 闹钟无法触发, 解锁打开应用, 触发闹钟 |
| AlarmManager | setExactAndAllowWhileIdle | 应用切后台锁屏, 闹钟无法触发, 解锁打开应用, 触发闹钟 |
| AlarmManager | ACTION_NEXT_ALARM_CLOCK_CHANGED  与getNextAlarmClock配合使用, 下一个闹钟被修改(删除)之后触发 | 应用切换至后台, 不会收到广播 |
| PowerManager | isIgnoringBatteryOptimizations | false |
| Settings | ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS | No Activity found to handle Intent |
| Intent | ACTION_TIME_TICK  每分钟开始(05:00)触发一次 | 应用切后台锁屏, 闹钟无法触发, 解锁打开应用, 收到最近一次的广播 |
| Intent | ACTION_SCREEN_ON  | 应用处于前台, 点亮屏幕, 不论是否解锁成功, 应用就能收到广播; 应用处于后台, 不会收到广播 | 
| RoleManager | createRequestRoleIntent(RoleManager.ROLE_DIALER) | 直接返回Activity.RESULT_CANCELED |

## 版本

### 0.0.2


### 0.0.1(废弃)

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
- [MD3 Component Compose](https://developer.android.google.cn/reference/kotlin/androidx/compose/material3/package-summary)
- [MD3 Component](https://github.com/material-components/material-components-android/blob/master/docs/components/)
- [腾讯地图 API](https://lbs.qq.com/mobile/androidLocationSDK/androidGeoGuide/androidGeoContinue)
- [MDC-Android](https://github.com/material-components/material-components-android)
- [OkHttp](https://github.com/square/okhttp)
- [Calendar Provder](https://developer.android.google.cn/guide/topics/providers/calendar-provider?hl=en)
- [Alarm](https://developer.android.google.cn/training/scheduling/alarms?hl=zh-cn)
- [Schedule Exact Alarms](https://developer.android.google.cn/about/versions/14/changes/schedule-exact-alarms?hl=en)
- [常用Intent](https://developer.android.google.cn/guide/components/intents-common?hl=zh-cn)
- [低电耗模式或待机模式下的优化](https://developer.android.google.cn/training/monitoring-device-state/doze-standby?hl=zh-cn#exemption-cases)
- [电池管理限制](https://developer.android.google.cn/topic/performance/power/power-details?hl=zh-cn)
- [Foreground Service](https://developer.android.google.cn/guide/components/foreground-services?hl=en)
- [Paging](https://developer.android.google.cn/topic/libraries/architecture/paging/v3-overview?hl=zh-cn)
- [修改状态栏颜色](https://developer.android.google.cn/develop/ui/views/layout/edge-to-edge)

## 课外知识

1. gradle 8.0要求的jdk 17
2. Material Theme Builder可以创建M3的主题, 但是国内无法访问, 求镜像资源