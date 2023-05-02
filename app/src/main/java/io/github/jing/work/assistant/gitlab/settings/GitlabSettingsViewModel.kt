package io.github.jing.work.assistant.gitlab.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jing.work.assistant.Constants

class GitlabSettingsViewModel(app: Application): AndroidViewModel(app) {

    private val preferences: SharedPreferences

    private val domainName: MutableLiveData<String> = MutableLiveData()
    private val ipAddress: MutableLiveData<String> = MutableLiveData()
    private val useHttps: MutableLiveData<Boolean> = MutableLiveData()
    private val apiVersion: MutableLiveData<String> = MutableLiveData()
    private val personalAccessToken: MutableLiveData<String> = MutableLiveData()

    init {
        preferences = app.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        domainName.value = preferences.getString(Constants.GITLAB_DOMAIN_NAME, Constants.GITLAB_DOMAIN_NAME_DEFAULT)
        ipAddress.value = preferences.getString(Constants.GITLAB_IP_ADDRESS, "")
        useHttps.value = preferences.getBoolean(Constants.GITLAB_USE_HTTPS, true)
        apiVersion.value = preferences.getString(Constants.GITLAB_API_VERSION, "v4")
        personalAccessToken.value = preferences.getString(Constants.GITLAB_PERSONAL_ACCESS_TOKEN, "")
    }

    fun domainName(): LiveData<String> {
        return domainName
    }

    fun ipAddress(): LiveData<String> {
        return ipAddress
    }

    fun useHttps(): LiveData<Boolean> {
        return useHttps
    }

    fun apiVersion(): LiveData<String> {
        return apiVersion
    }

    fun personalAccessToken(): LiveData<String> {
        return personalAccessToken
    }

    fun save(domainName: String, ipAddress: String, useHttps: Boolean, apiVersion: String, personalAccessToken: String) {
        preferences.edit().putString(Constants.GITLAB_DOMAIN_NAME, domainName)
            .putString(Constants.GITLAB_IP_ADDRESS, ipAddress)
            .putBoolean(Constants.GITLAB_USE_HTTPS, useHttps)
            .putString(Constants.GITLAB_API_VERSION, apiVersion)
            .putString(Constants.GITLAB_PERSONAL_ACCESS_TOKEN, personalAccessToken)
            .apply()
    }
}