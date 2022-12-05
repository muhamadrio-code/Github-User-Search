package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.*
import com.muhammadrio.githubuser.Event
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class SearchUserViewModel(
    private val userRepository: UserRepository,
) : UserViewModel(userRepository) {

    private var userPage = 1
    private lateinit var loginName: String

    private val tempUsers = mutableSetOf<User>()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = Transformations.switchMap(_users) { mUsers ->
        liveData {
            val ls = mUsers.map { mUser ->
                val isFavorite = userRepository.checkIsFavoriteUser(mUser.id)
                mUser.setIsFavorite(isFavorite)
                mUser
            }
            emit(ls)
        }
    }

    private val _showSelectionThemeDialog = MutableLiveData<Event<Boolean>>()
    val showSelectionThemeDialog: LiveData<Event<Boolean>> = _showSelectionThemeDialog

    fun refreshUsers(){
        _users.value ?: return
        if(_users.value!!.isEmpty()) return

        viewModelScope.launch {
            val list = mutableListOf<User>()
            users.value?.map {
                if (!userRepository.checkIsFavoriteUser(it.id) == it.isFavorite){
                    val newUser = it.copy()
                    list.add(newUser)
                } else {
                    list.add(it)
                }
            }
            _users.postValue(list)
        }
    }

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
            val result = userRepository.getUsersAtPage(loginName, userPage)
            if (result is Result.Success) setUsers(result.value)
        }
    }

    private fun setToLoadingState() {
        tempUsers.clear()
        requestLoadingState()
    }

    private fun handleSearchUsersResult(result: Result<List<User>>) {
        when (result) {
            is Result.Success -> handleResultSuccess(result.value)
            is Result.Failure -> requestErrorState(result.errorMessage)
        }
    }

    private fun setUsers(users: List<User>) {
        tempUsers += users
        _users.postValue(tempUsers.toList())
    }

    private fun handleResultSuccess(users: List<User>) {
        setUsers(users)
        if (users.isNotEmpty()) {
            requestSuccessState()
        } else {
            requestErrorState(
                ErrorMessage(
                    R.string.user_not_found_tittle,
                    R.string.user_not_found_message,
                    0
                )
            )
        }
    }

    fun requestThemeSelectionDialog() {
        _showSelectionThemeDialog.value = Event(true)
    }
}