package com.muhammadrio.githubuser.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails

class FakeUserRepository : UserRepository {

    private val users = mutableSetOf<User>()
    private val favoriteUsers = mutableSetOf<User>()
    private val observableUsers = MutableLiveData(users.toList())


    override suspend fun getUsers(q: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(userLogin: String): Result<UserDetails> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersAtPage(q: String, page: Int): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowers(userLogin: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowing(userLogin: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getFavoriteUsers(): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun checkIsFavoriteUser(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavoriteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun clearFavoriteUsers() {
        TODO("Not yet implemented")
    }
}