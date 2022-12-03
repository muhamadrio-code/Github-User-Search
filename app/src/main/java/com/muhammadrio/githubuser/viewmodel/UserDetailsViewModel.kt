package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val userRepo: UserRepository
) : UserViewModel(userRepo) {

    private var mUserLogin = ""

    private val _userDetails = MutableLiveData<UserDetails>()
    val userDetails: LiveData<UserDetails> = _userDetails

    fun getUserDetails(userLogin: String) {
        mUserLogin = userLogin
        viewModelScope.launch {
            val result = userRepo.getUserDetails(userLogin)
            val value = getResultValueOrNull(result)
            value?.let {
                if (userRepo.checkIsFavoriteUser(it.id)) {
                    it.isFavorite = true
                }
                _userDetails.postValue(it)
            }
        }
    }

    private fun getResultValueOrNull(result: Result<UserDetails>): UserDetails? {
        var value: UserDetails? = null
        when (result) {
            is Result.Failure -> {
                requestErrorState(result.errorMessage)
            }
            is Result.Success -> {
                requestSuccessState()
                value = result.value
            }
        }
        return value
    }
}