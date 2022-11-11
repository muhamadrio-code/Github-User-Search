package com.muhammadrio.githubuser.network

import okhttp3.Interceptor
import okhttp3.Response

class GithubUserApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("Authorization", "Bearer $AUTH_TOKEN")
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val AUTH_TOKEN = "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"
    }
}