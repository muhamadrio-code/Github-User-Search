package com.muhammadrio.githubuser.repositories

import androidx.lifecycle.LiveData
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.QueryParams
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails

interface UserRepository {

    suspend fun getUsers(queryParams: QueryParams) : Result<List<User>>

    suspend fun getUserDetails(userLogin: String): Result<UserDetails>

    suspend fun getFollowers(userLogin: String): Result<List<User>>

    suspend fun getFollowing(userLogin: String): Result<List<User>>

    fun getFavoriteUsers(): LiveData<List<User>>

    suspend fun insertFavoriteUser(user:User)

    suspend fun deleteFavoriteUser(user: User)

    suspend fun checkIsFavoriteUser(id: Int): Boolean

    suspend fun clearFavoriteUsers()
}