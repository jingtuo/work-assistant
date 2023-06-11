package io.github.jing.work.assistant.gitlab.mr.widget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CreateMrViewModel(val app: Application) : AndroidViewModel(app) {

    val branches: MutableLiveData<List<String>> = MutableLiveData()

    val users: MutableLiveData<List<User>> = MutableLiveData()

    private val mDisposable: CompositeDisposable = CompositeDisposable()

    private val toastMsg: MutableLiveData<String> = MutableLiveData()

    var assigneeId: Int? = null

    fun loadBranches(projectId: Int) {
        mDisposable.add(GitlabManager.instance.gitlab!!.projectBranches(projectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map { item -> item.name }
            }
            .subscribe { it ->
                branches.value = it
            })
    }

    fun loadUsers(projectId: Int) {
        mDisposable.add(GitlabManager.instance.gitlab!!.projectUsers(projectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                users.value = it
            })
    }

    override fun onCleared() {
        super.onCleared()
        mDisposable.dispose()
        mDisposable.clear()
    }

    fun setAssigneeUser(position: Int) {
        users.value?.let {
            if (position >= 0 && position < it.size) {
                assigneeId = it[position].id
            } else {
                assigneeId = -1
            }
        }
    }

    fun createMr(
        projectId: Int,
        sourceBranch: String,
        targetBranch: String,
        title: String
    ) {
        mDisposable.add(
            GitlabManager.instance.gitlab!!.createMr(
                projectId,
                sourceBranch, targetBranch, title,
                assigneeId = assigneeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    toastMsg.value = app.getString(R.string.create_success)
                }, { it -> toastMsg.value = it.message })
        )
    }
}