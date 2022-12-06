package com.muhammadrio.githubuser.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.muhammadrio.githubuser.MainDispatcherRule
import com.muhammadrio.githubuser.getOrAwaitValueTest
import com.muhammadrio.githubuser.repositories.FakeUserRepository
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class FavoriteUserViewModelTest {

    private lateinit var viewModel: FavoriteUserViewModel
    private lateinit var userRepository: FakeUserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        viewModel = FavoriteUserViewModel(userRepository)
    }

    @Test
    fun `test requestEvent liveData`() {
        viewModel.requestClearAll()

        val given = viewModel.requestEvent.getOrAwaitValueTest()
        assertTrue(given)
    }

    @Test
    fun `test requestEvent liveData when event finish`() {
        viewModel.requestEventFinish()

        val given = viewModel.requestEvent.getOrAwaitValueTest()
        assertFalse(given)
    }

    @Test
    fun `verify clearAllFavoriteUser`() = runTest {
        val mockRepo = Mockito.mock(UserRepository::class.java)
        val vm = FavoriteUserViewModel(mockRepo)
        vm.clearAllFavoriteUser()
        runCurrent()
        Mockito.verify(mockRepo, Mockito.times(1)).clearFavoriteUsers()
    }

    @Test
    fun `test favoriteUsers after cleared`() = runTest {
        val given = viewModel.favoriteUsers.getOrAwaitValueTest()
        viewModel.clearAllFavoriteUser()
        runCurrent()

        assertThat(given).isEmpty()
    }

    @Test
    fun `test favoriteUsers livedata`() = runTest {
        val user = userRepository.getUserForTest()
        viewModel.insertFavoriteUser(user)
        advanceUntilIdle()
        val given = viewModel.favoriteUsers.getOrAwaitValueTest()
        assertThat(given).isNotEmpty()
    }
}