package io.github.jing.work.assistant.gitlab.data

class MrRebase {

    var rebaseInProgress: Boolean = false
    var mergeError: String? = null

    fun isSuccess(): Boolean {
        return !rebaseInProgress && mergeError.isNullOrEmpty()
    }

}