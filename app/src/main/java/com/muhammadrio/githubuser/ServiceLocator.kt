package com.muhammadrio.githubuser

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.muhammadrio.githubuser.data.local.FavoritesUserDatabase
import com.muhammadrio.githubuser.data.remote.Retrofit
import com.muhammadrio.githubuser.repositories.UserRepository
import com.muhammadrio.githubuser.repositories.UserRepositoryImpl

object ServiceLocator {

    private var database: FavoritesUserDatabase? = null

    @Volatile
    var tasksRepository: UserRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): UserRepository {
        synchronized(this) {
            return tasksRepository ?: run {
                val repo = createUserRepository(context)
                tasksRepository = repo
                repo
            }
        }
    }

    private fun createUserRepository(context: Context) : UserRepository {
        return UserRepositoryImpl(
            createDataBase(context.applicationContext).favoritesUserDao,
            Retrofit.userApi
        )
    }

    private fun createDataBase(context: Context): FavoritesUserDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            FavoritesUserDatabase::class.java, "FavoriteUsers.db"
        ).build()
        database = result
        return result
    }

}