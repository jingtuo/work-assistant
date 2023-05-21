package io.github.jing.work.assistant.gitlab.data

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val headers: Headers): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        for (header in headers) {
            requestBuilder.addHeader(header.first, header.second)
        }
        return chain.proceed(requestBuilder.build())
    }
}