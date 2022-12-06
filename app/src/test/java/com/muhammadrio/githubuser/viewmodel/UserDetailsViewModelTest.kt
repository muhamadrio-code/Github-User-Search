package com.muhammadrio.githubuser.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.muhammadrio.githubuser.MainDispatcherRule
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.getOrAwaitValueTest
import com.muhammadrio.githubuser.repositories.FakeUserRepository
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class UserDetailsViewModelTest {

    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var userRepository: FakeUserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        viewModel = UserDetailsViewModel(userRepository)
    }

    @Test
    fun `verify getUserDetails was called once`() = runTest {
        val mock = mock(UserRepository::class.java)
        val vm = UserDetailsViewModel(mock)
        val login = "rio"
        vm.getUserDetails(login)
        runCurrent()
        verify(mock, times(1)).getUserDetails(login)
    }

    @Test
    fun `test userDetails livedata expect Success`() = runTest {
        val login = "rio"
        viewModel.getUserDetails(login)
        advanceUntilIdle()

        val given = viewModel.userDetails.getOrAwaitValueTest()
        assertThat(given).isNotNull()
    }

    @Test
    fun `check queryStatus on getUserDetails return success`() = runTest {
        val login = "rio"
        viewModel.getUserDetails(login)
        advanceUntilIdle()

        val status = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(status).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun `check queryStatus on getUserDetails return error`() = runTest {
        val login = "rio"
        userRepository.setShouldReturnError(true)
        viewModel.getUserDetails(login)
        advanceUntilIdle()

        val status = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(status).isInstanceOf(QueryStatus.OnFailure::class.java)
    }
}