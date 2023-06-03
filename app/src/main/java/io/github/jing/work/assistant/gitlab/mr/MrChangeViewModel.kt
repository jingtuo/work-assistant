package io.github.jing.work.assistant.gitlab.mr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.MrChange
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MrChangeViewModel(app: Application): AndroidViewModel(app) {

    var mDisposable: Disposable? = null

    val mrChanges: MutableLiveData<Array<MrChange>> = MutableLiveData()

    fun loadMrChanges(projectId: Int, mrIid: Int) {
        mDisposable = GitlabManager.instance.gitlab!!.mrChanges(projectId, mrIid)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                mrChanges.value = it.changes
            }
    }

    fun mrChanges(): LiveData<Array<MrChange>> {
        return mrChanges
    }

    override fun onCleared() {
        super.onCleared()
        mDisposable?.dispose()
    }
}