package io.github.jing.work.assistant.gitlab.mr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.data.MrChange
import io.github.jing.work.assistant.gitlab.data.MrState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MrChangeViewModel(val app: Application): AndroidViewModel(app) {

    private val mDisposable: CompositeDisposable = CompositeDisposable()

    private val mergeRequest: MutableLiveData<MergeRequest> = MutableLiveData()

    private val mrChanges: MutableLiveData<Array<MrChange>> = MutableLiveData()

    private val toastMsg: MutableLiveData<String> = MutableLiveData()

    fun loadMrChanges(projectId: Int, mrIid: Int) {
        mDisposable.add(GitlabManager.instance.gitlab!!.mrChanges(projectId, mrIid)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                mergeRequest.value = it
                mrChanges.value = it.changes
            })
    }

    fun mrChanges(): LiveData<Array<MrChange>> {
        return mrChanges
    }


    fun mergeMR() {
        val mr = mergeRequest.value ?: return
        mDisposable.add(
            GitlabManager.instance.gitlab!!.mergeMR(mr.projectId, mr.iid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (MrState.MERGED == it.state) {
                        toastMsg.value = app.getString(R.string.merge_success)
                    }
                }, {
                    toastMsg.value = it.message
                })
        )
    }

    fun rebaseMr(){
        val mr = mergeRequest.value ?: return
        mDisposable.add(
            GitlabManager.instance.gitlab!!.rebaseMR(mr.projectId, mr.iid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.rebaseInProgress) {
                        toastMsg.value = app.getString(R.string.rebasing)
                    } else {
                        if (it.mergeError.isNullOrEmpty()) {
                            toastMsg.value = app.getString(R.string.rebase_success)
                        } else {
                            toastMsg.value = it.mergeError
                        }
                    }
                }, {
                    toastMsg.value = it.message
                }))
    }

    fun closeMR() {
        val mr = mergeRequest.value ?: return
        mDisposable.add(GitlabManager.instance.gitlab!!.updateMR(
            mr.projectId,
            mr.iid,
            stateEvent = "close"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (MrState.MERGED == it.state) {
                    toastMsg.value = app.getString(R.string.close_success)
                }
            }, {
                toastMsg.value = it.message
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        mDisposable.dispose()
        mDisposable.clear()
    }

    fun toastMsg(): LiveData<String> {
        return toastMsg
    }
}