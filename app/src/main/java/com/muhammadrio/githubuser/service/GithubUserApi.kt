package com.muhammadrio.githubuser.service

import com.muhammadrio.githubuser.model.QueryResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubUserApi {

    @GET("/search/users")
    suspend fun searchUsers(
        @Query("q") q:String
    ) : Response<QueryResult>
}