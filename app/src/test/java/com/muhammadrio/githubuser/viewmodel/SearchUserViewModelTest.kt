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
    fun test_search_user_with_empty_string() = runTest {
        viewModel.searchUsers("")

        runCurrent()
        advanceUntilIdle()

        val users = viewModel.users.getOrAwaitValueTest()
        val queryStatus = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(users).isEmpty()
        assertThat(queryStatus).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun test_search_user_with_blank_string() = runTest {
        viewModel.searchUsers("   ")

        runCurrent()
        advanceUntilIdle()

        val users = viewModel.users.getOrAwaitValueTest()
        val queryStatus = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(users).isEmpty()
        assertThat(queryStatus).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun test_search_user_with_valid_string() = runTest {
        viewModel.searchUsers("rio")

        runCurrent()
        advanceUntilIdle()

        val users = viewModel.users.getOrAwaitValueTest()
        val queryStatus = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(users).isNotEmpty()
        assertThat(queryStatus).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun test_query_status_onLoading_when_search_user() = runTest {
        viewModel.searchUsers("")
        val given = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(given).isEqualTo(QueryStatus.OnLoading)
    }

    @Test
    fun test_search_user_next_page_expect_success() = runTest {
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
    fun test_search_user_next_page_expect_failure() = runTest {
        viewModel.searchUsers("rio")
        runCurrent()
        val given1 = viewModel.users.getOrAwaitValueTest()

        userRepository.setShouldReturnError(true)
        viewModel.searchNextPage()
        val queryStatus = viewModel.queryStatus.getOrAwaitValueTest()

        advanceUntilIdle()

        val given2 = viewModel.users.getOrAwaitValueTest()

        assertThat(given1).isEqualTo(given2)
        assertThat(queryStatus).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }


}