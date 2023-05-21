package io.github.jing.work.assistant.gitlab

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.jing.work.assistant.Constants
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.Project
import io.github.jing.work.assistant.gitlab.data.ProjectSource
import kotlinx.coroutines.flow.Flow


class GitlabViewModel(app: Application): AndroidViewModel(app) {

    private val preferences: SharedPreferences

    private val projects = MutableLiveData<List<Project>>()

    private var pager: Pager<Int, Project>? = null

    init {
        preferences = app.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun initGitlab() {
        val domainName = preferences.getString(Constants.GITLAB_DOMAIN_NAME, null)
        val ip = preferences.getString(Constants.GITLAB_IP_ADDRESS, null)
        val useHttps = preferences.getBoolean(Constants.GITLAB_USE_HTTPS, true)
        val apiVersion = preferences.getString(Constants.GITLAB_API_VERSION, Constants.GITLAB_API_VERSION_DEFAULT)
        val personalAccessToken = preferences.getString(Constants.GITLAB_PERSONAL_ACCESS_TOKEN, null)
        GitlabManager.instance.init(domainName, ip, useHttps, apiVersion!!, personalAccessToken)
    }

    fun projects(): LiveData<List<Project>> {
        return projects
    }

    fun search(search: String): Flow<PagingData<Project>>? {
        //当prefetchDistance=pageSize时, 首次加载会调用三次接口, 此处设置为1, 首次加载仅调用两次, 1次为首页, 2次为第二页
        //initialLoadSize默认为pageSize * 3
        pager = Pager(PagingConfig(initialLoadSize = 10, pageSize = 10, prefetchDistance = 1), 1
        ) { ProjectSource(GitlabManager.instance.gitlab!!, search) }
        return pager!!.flow.cachedIn(viewModelScope)
    }

    companion object {
        const val TAG = "GitlabViewModel"
    }
}