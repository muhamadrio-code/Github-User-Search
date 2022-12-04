package com.muhammadrio.githubuser.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails

class FakeUserRepository : UserRepository {

    private val usersPage1 = listOf(
        User(
            avatarUrl = "",
            eventsUrl = "",
            followersUrl = "",
            followingUrl = "",
            gistsUrl = "",
            gravatarId = "",
            htmlUrl = "",
            id = 1,
            login = "rio",
            nodeId = "",
            organizationsUrl = "",
            receivedEventsUrl = "",
            reposUrl = "",
            score = 0,
            siteAdmin = false,
            starredUrl = "",
            subscriptionsUrl = "",
            type = "",
            url = ""
        )
    )

    private val usersPage2 = listOf(
        User(
            avatarUrl = "",
            eventsUrl = "",
            followersUrl = "",
            followingUrl = "",
            gistsUrl = "",
            gravatarId = "",
            htmlUrl = "",
            id = 2,
            login = "mamang racing",
            nodeId = "",
            organizationsUrl = "",
            receivedEventsUrl = "",
            reposUrl = "",
            score = 0,
            siteAdmin = false,
            starredUrl = "",
            subscriptionsUrl = "",
            type = "",
            url = ""
        )
    )
    private val favoriteUsers = hashMapOf<Int, User>()
    private val userDetails = UserDetails(
        avatar_url = "",
        bio = "",
        blog = "",
        company = "",
        created_at = "",
        email = "",
        events_url = "",
        followers = 0,
        followers_url = "",
        following = 0,
        following_url = "",
        gists_url = "",
        gravatar_id = "",
        hireable = false,
        html_url = "",
        id = 1,
        location = "",
        login = "",
        name = "",
        node_id = "",
        organizations_url = "",
        public_gists = 0,
        public_repos = 0,
        received_events_url = "",
        repos_url = "",
        site_admin = false,
        starred_url = "",
        subscriptions_url = "",
        twitter_username = "",
        type = "",
        updated_at = "",
        url = "",
        isFavorite = false
    )

    private val observableFavoriteUsers = MutableLiveData(favoriteUsers.values.toList())

    private var shouldReturnError = false

    fun getUserForTest(): User = User(
        avatarUrl = "",
        eventsUrl = "",
        followersUrl = "",
        followingUrl = "",
        gistsUrl = "",
        gravatarId = "",
        htmlUrl = "",
        id = 1,
        login = "rio",
        nodeId = "",
        organizationsUrl = "",
        receivedEventsUrl = "",
        reposUrl = "",
        score = 0,
        siteAdmin = false,
        starredUrl = "",
        subscriptionsUrl = "",
        type = "",
        url = ""
    )

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    private fun refreshFavoriteUsers() {
        observableFavoriteUsers.value = favoriteUsers.values.toList()
    }

    override suspend fun getUsers(q: String): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Failure(ErrorMessage(0, 0, 0), Exception())
        } else {
            if (q.isEmpty() or q.isBlank()){
                Result.Success(emptyList())
            } else {
                Result.Success(usersPage1)
            }
        }
    }

    override suspend fun getUserDetails(userLogin: String): Result<UserDetails> {
        return if (shouldReturnError) {
            Result.Failure(ErrorMessage(0, 0, 0), Exception())
        } else {
            Result.Success(userDetails)
        }
    }

    override suspend fun getUsersAtPage(q: String, page: Int): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Failure(ErrorMessage(0, 0, 0), Exception())
        } else {
            Result.Success(usersPage1 + usersPage2)
        }
    }

    override suspend fun getFollowers(userLogin: String): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Failure(ErrorMessage(0, 0, 0), Exception())
        } else {
            Result.Success(usersPage1)
        }
    }

    override suspend fun getFollowing(userLogin: String): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Failure(ErrorMessage(0, 0, 0), Exception())
        } else {
            Result.Success(usersPage1)
        }
    }

    override fun getFavoriteUsers(): LiveData<List<User>> {
        return observableFavoriteUsers
    }

    override suspend fun checkIsFavoriteUser(id: Int): Boolean {
        return favoriteUsers[id] != null
    }

    override suspend fun insertFavoriteUser(user: User) {
        favoriteUsers[user.id] = user
        refreshFavoriteUsers()
    }

    override suspend fun deleteFavoriteUser(user: User) {
        favoriteUsers.remove(user.id)
        refreshFavoriteUsers()
    }

    override suspend fun clearFavoriteUsers() {
        favoriteUsers.clear()
        refreshFavoriteUsers()
    }
}