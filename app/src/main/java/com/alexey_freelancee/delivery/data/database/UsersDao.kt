package com.alexey_freelancee.delivery.data.database

import androidx.room.*
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.data.models.User

@Dao
interface UsersDao{
    @Query("SELECT * FROM users_table WHERE uid LIKE :id")
    suspend fun getUser(id:String) : User

    @Query("SELECT * FROM users_table")
    suspend fun loadAll():List<User>

    @Delete
    suspend fun delete(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user:User)
}