package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.network.Result
import com.muhammadrio.githubuser.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepo : UserRepository = UserRepository()

    private val _queryStatus = MutableLiveData<QueryStatus<List<User>>>(QueryStatus.OnEmpty)
    val queryStatus : LiveData<QueryStatus<List<User>>> = _queryStatus

    fun searchUsers(query:String) {
        _queryStatus.value = QueryStatus.OnLoading
        viewModelScope.launch {
            val queryResult = userRepo.searchUsers(query)
            _queryStatus.value = QueryStatus.OnFinished(queryResult)
        }
    }

    sealed class QueryStatus<out T> {
        object OnEmpty : QueryStatus<Nothing>()
        object OnLoading : QueryStatus<Nothing>()
        data class OnFinished<out T>(val result: Result<T>) : QueryStatus<T>()
    }
}