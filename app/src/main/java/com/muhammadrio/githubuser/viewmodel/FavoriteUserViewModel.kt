package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class FavoriteUserViewModel(
    private val userRepository: UserRepository
) : UserViewModel(userRepository) {

    private val _requestEvent = MutableLiveData(false)
    val requestEvent : LiveData<Boolean> = _requestEvent

    val favoriteUsers : LiveData<List<User>> = userRepository.getFavoriteUsers()

    fun requestClearAll(){
        _requestEvent.value = true
    }

    fun requestEventFinish(){
        _requestEvent.value = false
    }

    fun clearAllFavoriteUser(){
        viewModelScope.launch {
            userRepository.clearFavoriteUsers()
        }
    }
}