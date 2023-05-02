package io.github.jing.work.assistant

object Constants {

    const val PREFERENCE_NAME = "WORK_ASSISTANT"

    /**
     * 打卡信息
     */
    const val CLOCK_IN_INFO = "CLOCK_IN_INFO"


    const val LATITUDE = "LATITUDE"

    const val LONGITUDE = "LONGITUDE"

    //半径
    const val RADIUS = "RADIUS"

    const val ADDRESS = "ADDRESS"

    const val WORK_START_CLOCK_IN = "WORK_START_CLOCK_IN"
    const val WORK_END_CLOCK_IN = "WORK_END_CLOCK_IN"

    const val CHANNEL_ID_AUTO_CLOCK_IN = "AutoClockIn"

    const val GEOFENCE_TAG = "GEOFENCE_TAG"

    const val GEOFENCE_CLOCK_IN = "GEOFENCE_CLOCK_IN"

    const val START_TIME = "START_TIME"

    /**
     * 地理围栏半径, 单位是米
     */
    const val GEOFENCE_RADIUS_50 = 50.0

    const val TRIGGER_FLAG = "TRIGGER_FLAG"
    const val TRIGGER_FLAGS = "TRIGGER_FLAGS"
    const val DURATION = "DURATION"

    const val TRIGGER_FLAG_NONE = 0
    const val TRIGGER_FLAG_ALL = 15
    const val TRIGGER_FLAG_IN_RANGE = 1
    const val TRIGGER_FLAG_STAY_IN_RANGE = 2
    const val TRIGGER_FLAG_OUT_OF_RANGE = 4
    const val TRIGGER_FLAG_STAY_OUT_OF_RANGE = 8

    /**
     * 允许的最小灵活时间是5分钟
     */
    const val CLOCK_IN_FLEX_MINUTE_5 = 5

    const val CLOCK_IN_WORKER_DURATION_DEFAULT = 5


    const val GITLAB_DOMAIN_NAME = "GITLAB_DOMAIN_NAME"
    const val GITLAB_DOMAIN_NAME_DEFAULT = "gitlab.com"

    const val GITLAB_USE_HTTPS = "GITLAB_USE_HTTPS"

    const val GITLAB_API_VERSION = "GITLAB_API_VERSION"

    const val GITLAB_PERSONAL_ACCESS_TOKEN = "GITLAB_PERSONAL_ACCESS_TOKEN"
}