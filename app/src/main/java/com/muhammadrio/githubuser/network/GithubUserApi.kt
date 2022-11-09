package com.muhammadrio.githubuser.network

import com.muhammadrio.githubuser.model.QueryResult
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubUserApi {

    @GET("/search/users")
    suspend fun searchUsers(
        @Query("q") q:String
    ) : Response<QueryResult>

    @GET("/users/{username}")
    suspend fun getUserDetails(
        @Path("username") userLogin:String
    ) : Response<UserDetails>
}