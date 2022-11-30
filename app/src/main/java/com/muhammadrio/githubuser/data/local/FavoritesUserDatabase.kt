package com.muhammadrio.githubuser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muhammadrio.githubuser.model.User

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class FavoritesUserDatabase : RoomDatabase() {
    abstract val favoritesUserDao: FavoriteUserDao
}