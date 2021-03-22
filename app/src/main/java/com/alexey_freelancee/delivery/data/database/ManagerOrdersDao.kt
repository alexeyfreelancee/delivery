package com.alexey_freelancee.delivery.data.database

import androidx.room.*
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
@Dao
interface ManagerOrdersDao {

    @Query("SELECT * FROM manager_orders_table WHERE createTime == :createTime")
    suspend fun load(createTime:Long) : ManagerOrder

    @Query("SELECT * FROM manager_orders_table")
    suspend fun loadAll() : List<ManagerOrder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: ManagerOrder)


    @Delete
    fun delete(order: ManagerOrder)
}