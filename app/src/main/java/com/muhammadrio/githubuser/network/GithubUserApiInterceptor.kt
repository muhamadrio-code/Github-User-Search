package com.muhammadrio.githubuser.network

import okhttp3.Interceptor
import okhttp3.Response

class GithubUserApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/vnd.github+json")
            .build()
        return chain.proceed(request)
    }
}