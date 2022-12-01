package com.muhammadrio.githubuser.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.muhammadrio.githubuser.model.User

@Dao
interface FavoriteUserDao {

    @Query("SELECT * FROM user_table")
    fun getFavoriteUsers() : LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getFavoriteUser(id:Int) : LiveData<User>

    @Query("SELECT EXISTS (SELECT id FROM user_table WHERE id = :id)")
    suspend fun isFavoriteUser(id:Int) : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUser(user: User)

    @Query("DELETE FROM user_table")
    suspend fun clearFavoriteUser()

    @Delete
    suspend fun deleteFavoriteUser(user: User)
}