package com.muhammadrio.githubuser.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.muhammadrio.githubuser.MainDispatcherRule
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.getOrAwaitValueTest
import com.muhammadrio.githubuser.repositories.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ConnectedPeopleViewModelTest {

    private lateinit var viewModel: ConnectedPeopleViewModel
    private lateinit var userRepository: FakeUserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        viewModel = ConnectedPeopleViewModel(userRepository)
    }

    @Test
    fun `check query status isLoading on get followers`() = runTest {
        viewModel.getFollowers()

        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnLoading::class.java)
    }

    @Test
    fun `check query status isFailure on get followers expect failure`() = runTest {
        userRepository.setShouldReturnError(true)
        viewModel.getFollowers()
        runCurrent()
        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun `check query status isSuccess on get followers expect success`() = runTest {
        viewModel.getFollowers()
        runCurrent()
        advanceUntilIdle()
        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun `check query status isLoading on get following`() = runTest {
        viewModel.getFollowing()

        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnLoading::class.java)
    }

    @Test
    fun `check query status isFailure on get following expect failure`() = runTest {
        userRepository.setShouldReturnError(true)
        viewModel.getFollowing()
        runCurrent()
        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun `check query status isSuccess on get following expect success`() = runTest {
        viewModel.getFollowing()
        runCurrent()
        advanceUntilIdle()
        val state1 = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(state1).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun `check followers livedata value isNotEmpty on getFollower expect success`() = runTest {
        viewModel.getFollowers()
        advanceUntilIdle()
        val followers = viewModel.followers.getOrAwaitValueTest()
        assertThat(followers).isNotEmpty()
    }

    @Test
    fun `check followers livedata value isEmpty on getFollower expect failure`() = runTest {
        userRepository.setShouldReturnError(true)
        viewModel.getFollowers()
        advanceUntilIdle()
        val followers = viewModel.followers.getOrAwaitValueTest()
        assertThat(followers).isEmpty()
    }

    @Test
    fun `check following livedata value isNotEmpty on getFollowing expect success`() = runTest {
        viewModel.getFollowing()
        advanceUntilIdle()
        val following = viewModel.following.getOrAwaitValueTest()
        assertThat(following).isNotEmpty()
    }

    @Test
    fun `check followers livedata value isEmpty on getFollowing expect failure`() = runTest {
        userRepository.setShouldReturnError(true)
        viewModel.getFollowing()
        advanceUntilIdle()
        val following = viewModel.following.getOrAwaitValueTest()
        assertThat(following).isEmpty()
    }
}