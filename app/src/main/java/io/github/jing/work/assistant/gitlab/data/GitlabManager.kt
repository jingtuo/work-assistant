package io.github.jing.work.assistant.gitlab.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

/**
 *
 */
class GitlabManager private constructor() {

    var gitlab: Gitlab? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GitlabManager()
        }
    }

    fun init(
        domainName: String?, ip: String?, useHttps: Boolean = true, apiVersion: String = "v4",
        personalAccessToken: String?
    ) {
        if (personalAccessToken.isNullOrEmpty()) {
            return
        }
        if (domainName.isNullOrEmpty() && ip.isNullOrEmpty()) {
            return
        }
        var baseUrl = if (useHttps) {
            "https"
        } else {
            "http"
        }
        baseUrl += "://"
        val hosts = mutableMapOf<String, String>()
        if (domainName.isNullOrEmpty()) {
            baseUrl += ip
        } else {
            baseUrl += domainName
            if (!ip.isNullOrEmpty()) {
                hosts.put(domainName, ip)
            }
        }
        baseUrl += "/api/$apiVersion/"
        val headers = Headers.Builder()
            .add("Private-Token", personalAccessToken ?: "")
            .build()
        val client = OkHttpClient.Builder()
            .dns(Hosts(hosts))
            .addInterceptor(HeaderInterceptor(headers))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date::class.java, GitlabDateAdapter())
            .registerTypeAdapter(MrState::class.java, GitlabMrStateAdapter())
            .create()
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .baseUrl(baseUrl)
            .build()
        gitlab = retrofit.create(Gitlab::class.java)
    }
}