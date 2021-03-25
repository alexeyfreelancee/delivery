package com.alexey_freelancee.delivery.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.data.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Database(entities = [User::class, Order::class, ManagerOrder::class], version = 12)
@TypeConverters(com.alexey_freelancee.delivery.data.database.TypeConverters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDao(): UsersDao
    abstract fun ordersDao(): OrdersDao
    abstract fun managerOrdersDao(): ManagerOrdersDao
}


class TypeConverters(){
    @TypeConverter
    fun fromListToJson(list:List<Long>) :String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json:String) :List<Long>{
        val listType: Type = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(json, listType)
    }
}