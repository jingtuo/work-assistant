package io.github.jing.work.assistant.gitlab.data

import android.util.Log
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MergeRequestSource(private val service: Gitlab, private val pid: Int, private val search: String) :

    BaseRxPagingSource<MergeRequest>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, MergeRequest>> {
        val page = params.key ?: 1
        return service.projectMR(pid, search, state = MrState.ALL, page = page, sizeOfPage = params.loadSize)
            .subscribeOn(Schedulers.io())
            .map { data -> toLoadResult(page, data, params.loadSize) }
            .onErrorReturn {
                Log.e(TAG, it.message, it)
                LoadResult.Error(it)
            }
    }

    private fun toLoadResult(
        curPage: Int,
        data: List<MergeRequest>,
        loadSize: Int
    ): LoadResult<Int, MergeRequest> {
        val nextKey = if (data.size < loadSize) {
            null
        } else {
            curPage + 1
        }
        return LoadResult.Page(data, null, nextKey)
    }

    companion object {
        const val TAG = "MergeRequestSource"
    }
}