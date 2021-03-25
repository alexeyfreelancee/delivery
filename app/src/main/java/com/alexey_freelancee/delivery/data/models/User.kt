package com.alexey_freelancee.delivery.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users_table")
data class User(
    @PrimaryKey
    val uid: String = "",
    val type: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val fullName: String = "",
    val jnk :String = ""
)