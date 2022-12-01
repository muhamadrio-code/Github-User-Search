package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.repositories.UserRepository
import kotlinx.coroutines.launch

class ConnectedPeopleViewModel(
    private val userRepository: UserRepository
) : UserViewModel(userRepository) {

    private var mUserLogin = ""

    private val _followers = MutableLiveData<List<User>>()
    val followers: LiveData<List<User>> = _followers

    private val _following = MutableLiveData<List<User>>()
    val following: LiveData<List<User>> = _following

    fun setUserLogin(login:String){
        mUserLogin = login
    }

    fun getFollowers() {
        viewModelScope.launch {
            val result = userRepository.getFollowers(mUserLogin)
            val value = getResultValueOrEmpty(result, ErrorMessage(0, R.string.no_followers, 0))
            _followers.postValue(value)
        }
    }

    fun getFollowing() {
        viewModelScope.launch {
            val result = userRepository.getFollowing(mUserLogin)
            val value = getResultValueOrEmpty(result, ErrorMessage(0, R.string.no_following, 0))
            _following.postValue(value)
        }
    }

    fun refreshFollowers(){
        getFollowers()
    }

    fun refreshFollowing() {
        getFollowing()
    }

    private suspend fun getResultValueOrEmpty(result: Result<List<User>>, messageIfEmpty: ErrorMessage) : List<User> {
        var value : List<User> = emptyList()
        when (result) {
            is Result.Failure -> requestErrorState(null)
            is Result.Success -> {
                val followers = result.value
                if (followers.isEmpty()) {
                    requestErrorState(messageIfEmpty)
                } else {
                    val users = result.value.map { mUser ->
                        val isFavorite = userRepository.checkIsFavoriteUser(mUser.id)
                        if (isFavorite) mUser.setIsFavorite(true)
                        mUser
                    }
                    value = users
                    requestSuccessState()
                }
            }
        }
        return value
    }
}