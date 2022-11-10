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
        @Query("q") q:String,
        @Query("page") page:Int = 1,
    ) : Response<QueryResult>

    @GET("/users/{userLogin}")
    suspend fun getUserDetails(
        @Path("userLogin") userLogin:String
    ) : Response<UserDetails>

    @GET("/users/{userLogin}/followers")
    suspend fun getFollowers(
        @Path("userLogin") userLogin:String
    ) : Response<List<User>>

    @GET("/users/{userLogin}/following")
    suspend fun getFollowing(
        @Path("userLogin") userLogin:String
    ) : Response<List<User>>

}