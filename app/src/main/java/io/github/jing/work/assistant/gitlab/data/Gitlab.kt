package io.github.jing.work.assistant.gitlab.data

import retrofit2.http.GET

interface Gitlab {

    @GET("projects?simple=true")
    fun simpleProjects()
}