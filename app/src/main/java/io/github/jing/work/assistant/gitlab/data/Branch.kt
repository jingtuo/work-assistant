package io.github.jing.work.assistant.gitlab.data

class Branch(val name: String) {
    private var merged:Boolean = false
    private var protected: Boolean = false
    private var default: Boolean = false
    private var developersCanPush: Boolean = false
    private var developersCanMerge: Boolean = false
    private var canPush: Boolean = false
    private var commit: Commit? = null
}