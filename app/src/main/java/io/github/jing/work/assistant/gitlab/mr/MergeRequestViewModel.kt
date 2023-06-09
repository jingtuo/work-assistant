package io.github.jing.work.assistant.gitlab.mr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.data.MergeRequestSource
import io.github.jing.work.assistant.gitlab.data.MrState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import java.util.function.BiConsumer
import java.util.function.Consumer

class MergeRequestViewModel(val app: Application) : AndroidViewModel(app) {

    private var pager: Pager<Int, MergeRequest>? = null

    private val mDisposable: CompositeDisposable = CompositeDisposable()

    fun mergeRequests(id: Int, search: String): Flow<PagingData<MergeRequest>> {
        pager = Pager(
            PagingConfig(initialLoadSize = 10, pageSize = 10, prefetchDistance = 1), 1
        ) { MergeRequestSource(GitlabManager.instance.gitlab!!, id, search) }
        return pager!!.flow.cachedIn(viewModelScope)
    }


    override fun onCleared() {
        super.onCleared()
        mDisposable.dispose()
        mDisposable.clear()
    }
}
