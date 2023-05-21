package io.github.jing.work.assistant.gitlab.data

import android.util.Log
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 搜索工程
 */
class ProjectSource(private val service: Gitlab, private val search: String) :
    BaseRxPagingSource<Project>() {

    companion object {
        const val TAG = "SearchProjectSource"
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Project>> {
        Log.i(TAG, "load ${params.key}, ${params.loadSize}")
        val page = params.key ?: 1
        return service.simpleProjects(search, "last_activity_at", Sort.DESC, page, params.loadSize)
            .subscribeOn(Schedulers.io())
            .map { data -> toLoadResult(page, data, params.loadSize) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(curPage: Int, data: List<Project>, loadSize: Int): LoadResult<Int, Project> {
        val nextKey = if (data.size < loadSize) {
            null
        } else {
            curPage + 1
        }
        return LoadResult.Page(data, null, nextKey)
    }
}