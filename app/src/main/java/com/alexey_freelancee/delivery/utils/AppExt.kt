package com.alexey_freelancee.delivery.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.text.SimpleDateFormat
import java.util.*

const val TYPE_MANAGER = "manager"
const val TYPE_SUPPLIER = "supplier"
const val TYPE_CUSTOMER = "customer"

const val STATUS_COMPLETED = "Прибыл"
const val STATUS_PROCESSING = "В пути"
const val STATUS_PACKED = "Отгружен"

fun log(msg:Any?){
    Log.d("MEONER", msg.toString())
}
fun Context.toast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun getDateString(date:Long):String{

    val sdf = SimpleDateFormat("dd.MM HH:ss", Locale.getDefault())
   return sdf.format(Date(date))
}

fun isOnline(): Boolean {
    return try {
        val timeoutMs = 1500
        val sock = Socket()
        val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
        sock.connect(sockaddr, timeoutMs)
        sock.close()
        true
    } catch (e: IOException) {
        log("device status = disconnected")
        false
    }
}