package com.muhammadrio.githubuser

import android.app.Application

class MainApplication : Application() {

    val userRepository = ServiceLocator.provideTasksRepository(this)

}