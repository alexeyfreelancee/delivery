package com.alexey_freelancee.delivery.utils

import android.content.Context

class SharedPrefsUtil(private val context: Context) {

    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)


    fun getUserId():String?{
        return prefs.getString("uid",null)
    }

    fun setUserId(uid:String?){
        prefs.edit().putString("uid", uid).apply()
    }
}