package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.muhammadrio.githubuser.Event
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

open class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navDirection = MutableLiveData<Event<NavDirections>>()
    val navDirection : LiveData<Event<NavDirections>> = _navDirection

    private val _queryStatus = MutableLiveData<QueryStatus>(QueryStatus.OnEmpty)
    val queryStatus: LiveData<QueryStatus> = _queryStatus

    fun requestLoadingState(){
        _queryStatus.value = QueryStatus.OnLoading
    }

    fun requestSuccessState(){
        _queryStatus.value = QueryStatus.OnSuccess
    }

    fun requestErrorState(errorMessage: ErrorMessage?){
        _queryStatus.value = QueryStatus.OnFailure(errorMessage ?: ErrorMessage(0,0,-1))
    }

    fun requestNavigation(directions: NavDirections){
        _navDirection.value = Event(directions)
    }

    fun insertFavoriteUser(user: User) {
        viewModelScope.launch {
            userRepository.insertFavoriteUser(user)
        }
    }

    fun deleteFavoriteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteFavoriteUser(user)
        }
    }
}