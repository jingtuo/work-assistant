package io.github.jing.work.assistant.gitlab.data

import android.util.Log
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource

abstract class BaseRxPagingSource<T: Any>: RxPagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        Log.i("PagingSource",
            "getRefreshKey ${state.anchorPosition ?: 0} " +
                    "${state.closestPageToPosition(state.anchorPosition ?: 0)}"
        )
        val anchorPosition: Int = state.anchorPosition?: return null

        val page = state.closestPageToPosition(anchorPosition)
            ?: return null

        if (page.prevKey != null) {
            return page.prevKey!! + 1
        }

        return if (page.nextKey != null) {
            page.nextKey!! - 1
        } else null
    }

}