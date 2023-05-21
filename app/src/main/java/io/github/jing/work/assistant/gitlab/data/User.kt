package io.github.jing.work.assistant.gitlab.data

data class User(val id: Int, val name: String) {
    var username: String = ""
    var state: String = ""
    var avatarUrl: String = ""
    var webUrl: String = ""
}
