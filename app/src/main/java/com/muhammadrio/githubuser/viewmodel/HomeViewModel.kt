package com.muhammadrio.githubuser.viewmodel

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.Event
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.repository.UserRepository
import com.muhammadrio.githubuser.ui.dialogs.ThemeSelectionDialog
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private var userPage = 1
    private lateinit var loginName: String

    private val tempUsers = mutableSetOf<User>()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _queryStatus = MutableLiveData<QueryStatus>(QueryStatus.OnEmpty)
    val queryStatus: LiveData<QueryStatus> = _queryStatus

    private val _showSelectionThemeDialog = MutableLiveData<Event<Boolean>>()
    val showSelectionThemeDialog: LiveData<Event<Boolean>> = _showSelectionThemeDialog

    fun searchUsers(query: String) {
        loginName = query
        setToLoadingState()
        viewModelScope.launch {
            val queryResult = userRepository.getUsers(query)
            handleSearchUsersResult(queryResult)
        }
    }

    fun searchNextPage() {
        viewModelScope.launch {
            userPage++
            val result = userRepository.getUsersAtPage(loginName,userPage)
            if (result is Result.Success) setUsers(result.value)
        }
    }

    private fun setToLoadingState() {
        tempUsers.clear()
        _queryStatus.value = QueryStatus.OnLoading
    }

    private fun handleSearchUsersResult(result: Result<List<User>>) {
        when (result) {
            is Result.Success -> handleResultSuccess(result.value)
            is Result.Failure -> setFailureStatusMessage(result.errorMessage)
        }
    }

    private fun setUsers(users: List<User>){
        tempUsers += users
        _users.postValue(tempUsers.toList())
    }

    private fun handleResultSuccess(users: List<User>) {
        setUsers(users)
        _queryStatus.value = if (users.isNotEmpty()) {
            QueryStatus.OnSuccess
        } else {
            QueryStatus.OnFailure(
                ErrorMessage(
                    R.string.user_not_found_tittle,
                    R.string.user_not_found_message,
                    0
                )
            )
        }
    }

    private fun setFailureStatusMessage(errorMessage: ErrorMessage) {
        _queryStatus.value = QueryStatus.OnFailure(errorMessage)
    }

    fun onThemeMenuClicked(){
        _showSelectionThemeDialog.value = Event(true)
    }
}