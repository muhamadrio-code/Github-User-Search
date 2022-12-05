package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.*
import com.muhammadrio.githubuser.Event
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.combineWith
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.QueryParams
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class SearchUserViewModel(
    private val userRepository: UserRepository,
) : UserViewModel(userRepository) {

    private var currentQueryParams: QueryParams? = null

    private val tempUsers = mutableSetOf<User>()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users.combineWith(userRepository.getFavoriteUsers()) { mUsers, favUsers ->
        if (mUsers == null) return@combineWith emptyList()
        if (favUsers == null) return@combineWith mUsers

        val hashMap = hashMapOf<Int,User>()
        val newList = mutableListOf<User>()
        favUsers.forEach { user -> hashMap[user.id] = user }
        mUsers.forEach { user ->
            val isFavorite = hashMap[user.id] != null
            val copy = user.copy()
            copy.setIsFavorite(isFavorite)
            newList.add(copy)
        }
        newList
    }

    private val _showSelectionThemeDialog = MutableLiveData<Event<Boolean>>()
    val showSelectionThemeDialog: LiveData<Event<Boolean>> = _showSelectionThemeDialog


    fun searchUsers(query: String) {
        currentQueryParams = QueryParams(query = query)
        setToLoadingState()
        currentQueryParams?.let {
            viewModelScope.launch {
                val queryResult = userRepository.getUsers(it)
                handleSearchUsersResult(queryResult)
            }
        }
    }

    fun searchNextPage() {
        currentQueryParams ?: return
        viewModelScope.launch {
            currentQueryParams = currentQueryParams?.let {
                QueryParams(it.query, page = it.page + 1)
            }

            val result = userRepository.getUsers(currentQueryParams!!)
            if (result is Result.Success) setUsers(result.value)
        }
    }

    private fun setToLoadingState() {
        tempUsers.clear()
        requestLoadingState()
    }

    private fun handleSearchUsersResult(result: Result<List<User>>){
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