package com.alexey_freelancee.delivery.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders_table")
data class Order(
    val storageAddress: String = "",
    val estimateTime: Long = 0,
    val index: Int= 0,
    val weight: Double =0.0,
    val customerName: String= "",
    val customerPhone:String= "",
    var status:String= "",
    val comment:String= "",
    @PrimaryKey
    val createTime:Long = 0
)
