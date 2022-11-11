package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.network.ErrorMessage
import com.muhammadrio.githubuser.network.QueryStatus
import com.muhammadrio.githubuser.network.Result
import com.muhammadrio.githubuser.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepo: UserRepository = UserRepository()
    private var currentPage = 1
    private lateinit var loginName: String

    private val tempUsers = mutableSetOf<User>()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _queryStatus = MutableLiveData<QueryStatus>(QueryStatus.OnEmpty)
    val queryStatus: LiveData<QueryStatus> = _queryStatus

    fun searchUsers(query: String) {
        loginName = query
        setToLoadingState()
        viewModelScope.launch {
            val queryResult = userRepo.searchUsers(query)
            handleSearchUsersResult(queryResult)
        }
    }

    fun searchNextPage() {
        viewModelScope.launch {
            currentPage++
            val result = userRepo.searchUsers(loginName, currentPage)
            if (result is Result.Success) handleUsers(result.value)
        }
    }

    private fun setToLoadingState() {
        tempUsers.clear()
        _queryStatus.value = QueryStatus.OnLoading
    }

    private fun handleSearchUsersResult(result: Result<List<User>>) {
        when (result) {
            is Result.Success -> handleUsers(result.value)
            is Result.Failure -> setFailureStatusMessage(result.errorMessage)
        }
    }

    private fun handleUsers(users: List<User>) {
        if (users.isNotEmpty()) {
            tempUsers += users
            _users.postValue(tempUsers.toList())
        }
        _queryStatus.value = QueryStatus.OnSuccess
    }

    private fun setFailureStatusMessage(errorMessage: ErrorMessage) {
        _queryStatus.value = QueryStatus.OnFailure(errorMessage)
    }
}