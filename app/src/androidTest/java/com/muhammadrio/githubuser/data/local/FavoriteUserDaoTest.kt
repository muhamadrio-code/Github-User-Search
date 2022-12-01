package com.muhammadrio.githubuser.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.muhammadrio.githubuser.getOrAwaitValue
import com.muhammadrio.githubuser.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavoriteUserDaoTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    private lateinit var database: FavoritesUserDatabase
    private lateinit var dao: FavoriteUserDao

    private val userTest = User(
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

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavoritesUserDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoritesUserDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_favorite_user() = runTest {
        dao.insertFavoriteUser(userTest)

        val allFavoriteUser = dao.getFavoriteUsers().getOrAwaitValue()

        assertThat(allFavoriteUser).contains(userTest)
    }

    @Test
    fun test_get_favorite_user() = runTest {
        dao.insertFavoriteUser(userTest)

        val allFavoriteUser = dao.getFavoriteUser(1).getOrAwaitValue()

        assertThat(allFavoriteUser).isEqualTo(userTest)
    }

    @Test
    fun delete_favorite_user() = runTest {
        dao.insertFavoriteUser(userTest)
        dao.deleteFavoriteUser(userTest)

        val allFavoriteUser = dao.getFavoriteUsers().getOrAwaitValue()

        assertThat(allFavoriteUser).doesNotContain(userTest)
    }

    @Test
    fun clear_all_favorite_user() = runTest {
        val userTest1 = User(
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
        val userTest2 = User(
            avatarUrl = "",
            eventsUrl = "",
            followersUrl = "",
            followingUrl = "",
            gistsUrl = "",
            gravatarId = "",
            htmlUrl = "",
            id = 2,
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
        val userTest3 = User(
            avatarUrl = "",
            eventsUrl = "",
            followersUrl = "",
            followingUrl = "",
            gistsUrl = "",
            gravatarId = "",
            htmlUrl = "",
            id = 3,
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

        dao.insertFavoriteUser(userTest1)
        dao.insertFavoriteUser(userTest2)
        dao.insertFavoriteUser(userTest3)

        dao.clearFavoriteUser()

        val allFavoriteUser = dao.getFavoriteUsers().getOrAwaitValue()
        assertThat(allFavoriteUser).isEmpty()
    }


    @Test
    fun test_favorite_user_with_given_id_exist_return_true() = runTest {
        val userTest1 = User(
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

        dao.insertFavoriteUser(userTest1)

        val given = dao.isFavoriteUser(userTest1.id)

        assertThat(given).isTrue()
    }

    @Test
    fun test_favorite_user_with_given_id_not_exist_return_false() = runTest {
        val userTest1 = User(
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

        dao.insertFavoriteUser(userTest1)

        val given = dao.isFavoriteUser(0)

        assertThat(given).isFalse()
    }





}