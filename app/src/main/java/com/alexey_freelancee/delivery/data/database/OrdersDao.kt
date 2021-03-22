package com.alexey_freelancee.delivery.data.database

import androidx.room.*
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.data.models.User

@Dao
interface OrdersDao {
    @Query("SELECT * FROM orders_table WHERE :createTime == createTime")
    suspend fun load(createTime:Long) : Order

    @Query("SELECT * FROM orders_table")
    suspend fun loadAll() : List<Order>

    @Delete
    suspend fun delete(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order:Order)
}