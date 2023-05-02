package io.github.jing.work.assistant.gitlab.data

import java.util.Date

data class Project(val id: Int, val name: String) {
    //simple
    private var description: String = ""
    private var defaultBranch: String? = null
    private var sshUrlToRepo = ""
    private var httpUrlToRepo = ""
    private var webUrl = ""
    private var readmeUrl = ""
    private var tagList: Array<String>? = null
    private var createdAt: Date? = null
    private var lastActivityAt: Date? = null

    //其他
    private var visibility: String? = null
}
