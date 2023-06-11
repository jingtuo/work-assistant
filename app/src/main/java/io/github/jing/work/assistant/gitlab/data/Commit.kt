package io.github.jing.work.assistant.gitlab.data

import java.util.Date

class Commit(val id: String) {
    private var shortId: String? = null
    private var authorName: String? = null
    private var authorEmail: String? = null
    private var committedDate: Date? = null
    private var committerName: String? = null
    private var committerEmail: String? = null
    private var title: String? = null
    private var message: String? = null
}