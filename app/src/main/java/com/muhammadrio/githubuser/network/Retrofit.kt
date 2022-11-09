package com.muhammadrio.githubuser.network

import com.google.gson.GsonBuilder
import com.muhammadrio.githubuser.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {

    const val CONNECTION_ERROR_CODE = 1600
    const val UNKNOWN_ERROR_CODE = 1580

    private val client = OkHttpClient.Builder().apply {
        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        addInterceptor(GithubUserApiInterceptor())
        addInterceptor(loggingInterceptor)
    }.build()

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            ))
            .client(client)
            .build()
    }

    val userApi: GithubUserApi by lazy {
        instance.create(GithubUserApi::class.java)
    }
}