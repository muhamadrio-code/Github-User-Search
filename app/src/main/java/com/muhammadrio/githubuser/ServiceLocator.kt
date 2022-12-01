package com.muhammadrio.githubuser

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.muhammadrio.githubuser.data.remote.Retrofit
import com.muhammadrio.githubuser.repository.UserRepository
import com.muhammadrio.githubuser.repository.UserRepositoryImpl
import com.muhammadrio.githubuser.data.local.FavoritesUserDatabase
import com.muhammadrio.githubuser.ui.dialogs.ThemeSelectionDialog

object ServiceLocator {

    private val lock = Any()
    private var database: FavoritesUserDatabase? = null

    @Volatile
    var tasksRepository: UserRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): UserRepository {
        synchronized(this) {
            return tasksRepository ?: createUserRepository(context)
        }
    }

    private fun createUserRepository(context: Context) : UserRepository {
        return UserRepositoryImpl(
            createDataBase(context).favoritesUserDao,
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

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {

            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }

}