package com.muhammadrio.githubuser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhammadrio.githubuser.repositories.UserRepository

class UserViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass){
            when{
                isAssignableFrom(FavoriteUserViewModel::class.java) -> FavoriteUserViewModel(userRepository)
                isAssignableFrom(UserDetailsViewModel::class.java) -> UserDetailsViewModel(userRepository)
                isAssignableFrom(SearchUserViewModel::class.java) -> SearchUserViewModel(userRepository)
                isAssignableFrom(ConnectedPeopleViewModel::class.java) -> ConnectedPeopleViewModel(userRepository)
                else -> throw UnsupportedOperationException()
            }
        } as T
    }
}