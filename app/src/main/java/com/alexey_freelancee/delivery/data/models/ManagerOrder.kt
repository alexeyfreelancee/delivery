package com.alexey_freelancee.delivery.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manager_orders_table")
data class ManagerOrder(

    var status:String = "",
    val weight:Double = 0.0,
    @PrimaryKey
    val createTime:Long = 0,
    val estimateTime:Long = 0,
    val subOrders:List<Long> = emptyList()
)