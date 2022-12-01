package com.muhammadrio.githubuser

import android.app.Application
import androidx.fragment.app.DialogFragment
import com.muhammadrio.githubuser.repository.UserRepository

class MainApplication : Application() {

    val userRepository : UserRepository
        get() = ServiceLocator.provideTasksRepository(this)
}