package io.github.jing.work.assistant.gitlab.project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.data.MergeRequestSource
import kotlinx.coroutines.flow.Flow

class ProjectViewModel(app: Application): AndroidViewModel(app) {

    private var pager: Pager<Int, MergeRequest>? = null


    fun mergeRequests(id: Int, search: String): Flow<PagingData<MergeRequest>> {
        pager = Pager(
            PagingConfig(initialLoadSize = 10, pageSize = 10, prefetchDistance = 1), 1
        ) { MergeRequestSource(GitlabManager.instance.gitlab!!, id, search) }
        return pager!!.flow.cachedIn(viewModelScope)
    }
}