package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.network.ErrorMessage
import com.muhammadrio.githubuser.network.Result
import com.muhammadrio.githubuser.repository.UserRepository
import kotlinx.coroutines.launch

class UserDetailsViewModel : ViewModel() {

    private val userRepo: UserRepository = UserRepository()
    private var mUserLogin = ""

    private val _userDetails = MutableLiveData<Result<UserDetails>>()
    val userDetails: LiveData<Result<UserDetails>> = _userDetails

    private val _followers = MutableLiveData<Result<List<User>>>()
    val followers: LiveData<Result<List<User>>> = _followers

    private val _following = MutableLiveData<Result<List<User>>>()
    val following: LiveData<Result<List<User>>> = _following

    fun getUserDetails(userLogin: String) {
        mUserLogin = userLogin
        viewModelScope.launch {
            val result = userRepo.getUserDetails(userLogin)
            _userDetails.postValue(result)
        }
    }

    fun getFollowersList(userLogin: String) {
        viewModelScope.launch {
            val result = userRepo.getFollowers(userLogin)
            _followers.postValue(result)
        }
    }

    fun getFollowingList(userLogin: String) {
        viewModelScope.launch {
            val result = userRepo.getFollowing(userLogin)
            _following.postValue(result)
        }
    }

    fun refreshOnFailure() {
        if (followers.value is Result.Failure) getFollowersList(mUserLogin)
        if (following.value is Result.Failure) getFollowingList(mUserLogin)
    }


}