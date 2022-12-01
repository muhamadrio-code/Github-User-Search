package com.muhammadrio.githubuser.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val userRepo: UserRepository
) : UserViewModel(userRepo) {

    private var mUserLogin = ""

    private val _userDetails = MutableLiveData<Result<UserDetails>>()
    val userDetails: LiveData<Result<UserDetails>> = _userDetails

    fun getUserDetails(userLogin: String) {
        mUserLogin = userLogin
        viewModelScope.launch {
            val result = userRepo.getUserDetails(userLogin)
            _userDetails.postValue(result)
        }
    }
}