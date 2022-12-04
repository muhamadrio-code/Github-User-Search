package com.muhammadrio.githubuser.repositories

import androidx.lifecycle.LiveData
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails

interface UserRepository {

    suspend fun getUsers(q:String) : Result<List<User>>

    suspend fun getUserDetails(userLogin: String): Result<UserDetails>

    suspend fun getUsersAtPage(q: String, page: Int): Result<List<User>>

    suspend fun getFollowers(userLogin: String): Result<List<User>>

    suspend fun getFollowing(userLogin: String): Result<List<User>>

    fun getFavoriteUsers(): LiveData<List<User>>

    suspend fun checkIsFavoriteUser(id:Int): Boolean

    suspend fun insertFavoriteUser(user:User)

    suspend fun deleteFavoriteUser(user: User)

    suspend fun clearFavoriteUsers()
}