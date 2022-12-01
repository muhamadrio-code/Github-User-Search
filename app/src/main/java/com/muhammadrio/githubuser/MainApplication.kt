package com.muhammadrio.githubuser

import android.app.Application
import com.muhammadrio.githubuser.repositories.UserRepository

class MainApplication : Application() {

    val userRepository : UserRepository
        get() = ServiceLocator.provideTasksRepository(this)
}