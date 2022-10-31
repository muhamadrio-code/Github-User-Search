package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepo : UserRepository = UserRepository()

    val searchEvent = userRepo.searchEvent

    fun searchUsers(query:String) {
        viewModelScope.launch {
            userRepo.searchUsers(query)
        }
    }
}