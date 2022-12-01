package com.muhammadrio.githubuser.viewmodel

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhammadrio.githubuser.repository.UserRepository
import com.muhammadrio.githubuser.ui.dialogs.ThemeSelectionDialog

class HomeViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(userRepository) as T
}

class UserDetailsViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        UserDetailsViewModel(userRepository) as T
}