package com.muhammadrio.githubuser.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.muhammadrio.githubuser.MainDispatcherRule
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.getOrAwaitValueTest
import com.muhammadrio.githubuser.repositories.FakeUserRepository
import com.muhammadrio.githubuser.repositories.UserRepository
import com.muhammadrio.githubuser.ui.fragment.SearchUserFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class UserViewModelTest {

    private lateinit var viewModel: UserViewModel
    private lateinit var userRepository: FakeUserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        viewModel = UserViewModel(userRepository)
    }

    @Test
    fun `test requestLoadingState`() {
        viewModel.requestLoadingState()
        val given = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(given).isInstanceOf(QueryStatus.OnLoading::class.java)
    }

    @Test
    fun `test requestSuccessState`() {
        viewModel.requestSuccessState()
        val given = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(given).isInstanceOf(QueryStatus.OnSuccess::class.java)
    }

    @Test
    fun `test requestErrorState`() {
        viewModel.requestErrorState(ErrorMessage(0, 0, 0))
        val given = viewModel.queryStatus.getOrAwaitValueTest()
        assertThat(given).isInstanceOf(QueryStatus.OnFailure::class.java)
    }

    @Test
    fun `test NavDirections livedata`() {
        val direction = SearchUserFragmentDirections.actionSearchUserFragmentToFavoriteUserFragment()
        viewModel.requestNavigation(direction)
        val given = viewModel.navDirection.getOrAwaitValueTest()
        val value = given.peekContent()
        assertThat(value).isEqualTo(direction)
    }

    @Test
    fun `verify insert favorite user was called`() = runTest {
        val mockRepo = mock(UserRepository::class.java)
        val vm = UserViewModel(mockRepo)
        val user = userRepository.getUserForTest()
        vm.insertFavoriteUser(user)
        runCurrent()
        verify(mockRepo, times(1)).insertFavoriteUser(user)
    }

    @Test
    fun `test delete favorite user`() = runTest {
        val mockRepo = mock(UserRepository::class.java)
        val vm = UserViewModel(mockRepo)
        val user = userRepository.getUserForTest()
        vm.deleteFavoriteUser(user)
        runCurrent()
        verify(mockRepo, times(1)).deleteFavoriteUser(user)
    }
}