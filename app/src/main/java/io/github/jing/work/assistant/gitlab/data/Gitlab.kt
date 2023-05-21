package io.github.jing.work.assistant.gitlab.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * page: 页码, 默认值1
 * per_page: 每页的数据个数, 默认值20, 最大值100
 */
interface Gitlab {

    @GET("projects?simple=true")
    fun simpleProjects(
        @Query("search") search: String = "",
        @Query("order_by") orderBy: String = "created_at",
        @Query("sort") sort: Sort = Sort.DESC,
        @Query("page") page: Int = 1,
        @Query("per_page") sizeOfPage: Int = 20
    ): Single<List<Project>>


    /**
     * scope可取值: projects, issues, merge_requests, milestones, snippet_titles, snippet_blobs, users.
     * scope不同, 返回结果不同
     */
    @GET("search?scope=projects")
    fun searchProjects(
        @Query("search") search: String,
        @Query("page") page: Int = 1,
        @Query("per_page") sizeOfPage: Int = 20
    ): Single<List<Project>>

    /**
     * MR审核通过
     */
    @POST("projects/{pid}/merge_requests/{mrIid}/approve")
    fun approveMR(@Path("pid") pid: Int, @Path("mrIid") mrIid: Int): Single<Int>

    /**
     * 撤销MR审核通过
     */
    @POST("projects/{pid}/merge_requests/{mrIid}/unapprove")
    fun unApproveMR(@Path("pid") pid: Int, @Path("mrIid") mrIid: Int): Single<Int>

    /**
     * 仅请求分配给当前用户的MR
     */
    @GET("projects/{pid}/merge_requests?scope=assigned-to-me")
    fun projectMR(
        @Path("pid") pid: Int,
        @Query("search") search: String, //匹配标题和描述
        @Query("state") state: MrState = MrState.ALL,
        @Query("order_by") orderBy: String = "created_at",
        @Query("sort") sort: Sort = Sort.DESC,
        @Query("page") page: Int = 1,
        @Query("per_page") sizeOfPage: Int = 20
    ): Single<List<MergeRequest>>

    @GET("projects/{pid}/merge_requests/{mrIid}/changes")
    fun mrChanges(@Path("pid") pid: Int, @Path("mrIid") mrIid: Int)

    @PUT("projects/{pid}/merge_requests/{mrIid}/merge")
    fun mergeMR(@Path("pid") pid: Int, @Path("mrIid") mrIid: Int): Single<MergeRequest>

    @PUT("projects/{pid}/merge_requests/{mrIid}/rebase")
    fun rebaseMR(@Path("pid") pid: Int, @Path("mrIid") mrIid: Int)

    @PUT("projects/{pid}/merge_requests/{mrIid}")
    fun updateMR(
        @Path("pid") pid: Int, @Path("mrIid") mrIid: Int,
        @Field("title") title: String,
        @Field("state_event") stateEvent: String = "close" //close、reopen
    )
}