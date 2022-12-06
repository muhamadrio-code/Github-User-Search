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
class SearchUserViewModelTest {

    private lateinit var viewModel: SearchUserViewModel
    private lateinit var userRepository: FakeUserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        viewModel = SearchUserViewModel(userRepository)
    }

    @Test
    fun `test query status onEmpty as initial status`() = runTest {
        val given = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(given).isInstanceOf(QueryStatus.OnEmpty::class.java)
    }

    @Test
    fun `check query status isLoading on searchUser`(){
        viewModel.searchUsers("")
        val status = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(status).isInstanceOf(QueryStatus.OnLoading::class.java)
    }

    @Test
    fun `check query status isSuccess on searchUser finish expect success`() = runTest {
        viewModel.searchUsers("rio")
        runCurrent()
        advanceUntilIdle()
        val status = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(status).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun `check query status isFailure on searchUser finish expect failure`() = runTest {
        viewModel.searchUsers("")
        runCurrent()
        advanceUntilIdle()
        val status = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(status).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun `test search user with empty string expect users livedata return empty`() = runTest {
        viewModel.searchUsers("")
        runCurrent()
        advanceUntilIdle()
        val users = viewModel.users.getOrAwaitValueTest()
        assertThat(users).isEmpty()
    }

    @Test
    fun `test search user with blank string expect users livedata return empty`() = runTest {
        viewModel.searchUsers("   ")
        runCurrent()
        advanceUntilIdle()
        val users = viewModel.users.getOrAwaitValueTest()
        assertThat(users).isEmpty()
    }

    @Test
    fun `test search user with valid string expect users livedata return NotEmpty`() = runTest {
        viewModel.searchUsers("rio")
        runCurrent()
        advanceUntilIdle()
        val users = viewModel.users.getOrAwaitValueTest()
        assertThat(users).isNotEmpty()
    }

    @Test
    fun `test search users next page expect success`() = runTest {
        viewModel.searchUsers("rio")
        runCurrent()

        val given1 = viewModel.users.getOrAwaitValueTest()

        viewModel.searchNextPage()
        advanceUntilIdle()

        val given2 = viewModel.users.getOrAwaitValueTest()

        assertThat(given1.size).isLessThan(given2.size)
        assertThat(given1).isNotEqualTo(given2)
        assertThat(given2).containsNoDuplicates()
        assertThat(given2).containsAtLeastElementsIn(given1)
    }

    @Test
    fun `test search users next page expect failure`() = runTest {
        viewModel.searchUsers("rio")
        runCurrent()
        val given1 = viewModel.users.getOrAwaitValueTest()

        userRepository.setShouldReturnError(true)
        viewModel.searchNextPage()
        advanceUntilIdle()

        val given2 = viewModel.users.getOrAwaitValueTest()

        assertThat(given1).isEqualTo(given2)
    }


}