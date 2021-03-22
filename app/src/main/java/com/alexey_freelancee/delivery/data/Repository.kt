package com.alexey_freelancee.delivery.data

import com.alexey_freelancee.delivery.data.database.AppDatabase
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.data.models.User
import com.alexey_freelancee.delivery.utils.SharedPrefsUtil
import com.alexey_freelancee.delivery.utils.isOnline
import com.alexey_freelancee.delivery.utils.log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Repository(
    private val prefs: SharedPrefsUtil,
    private val db: AppDatabase
) {

    fun checkLogin() = prefs.getUserId() != null

    fun loadUid() :String? = prefs.getUserId()

    fun setUserId(uid: String) = prefs.setUserId(uid)

    suspend fun createManagerOrder(order:ManagerOrder):Boolean{
        return suspendCoroutine { continuation ->
            FirebaseDatabase.getInstance()
                .getReference("manager_orders")
                .child(order.createTime.toString())
                .setValue(order)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener{
                    continuation.resume(false)
                }
        }
    }
    suspend fun createOrder(order: Order) : Boolean{
        return suspendCoroutine { continuation ->
            FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(order.createTime.toString())
                .setValue(order)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener{
                    continuation.resume(false)
                }
        }
    }
    suspend fun doRegister(user: User, password: String): Boolean {
        return suspendCoroutine { continuation ->

            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    FirebaseDatabase
                        .getInstance()
                        .getReference("users")
                        .child(user.uid).setValue(user)
                        .addOnSuccessListener {
                            continuation.resume(true)
                        }
                        .addOnFailureListener {
                            log(it.message)
                            continuation.resume(false)
                        }

                }
                .addOnFailureListener {
                    log(it.message)
                    continuation.resume(false)
                }
        }
    }

    suspend fun doLogin(uid: String?, email: String?, pass: String?): Boolean {
        return suspendCoroutine { continuation ->
            if (uid == null || email.isNullOrBlank() || pass.isNullOrBlank()) continuation.resume(
                false
            )
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    log(it.message)
                    continuation.resume(false)
                }
        }

    }

    suspend fun loadOrderByTime(createTime:Long) : Order?{
        return loadOrders().find {it.createTime == createTime}
    }
    suspend fun loadUserByEmail(email: String?): User? {
        return loadUsers().find { it.email.equals(email, ignoreCase = true) }
    }

    suspend fun loadUserByUid(uid: String?): User? {
        return loadUsers().find { it.uid.equals(uid, ignoreCase = true) }
    }

    suspend fun loadManagerOrderByTime(createTime: Long):ManagerOrder?{
        return loadManagerOrders().find { it.createTime == createTime }
    }

    suspend fun loadManagerOrders():List<ManagerOrder>{
        if(isOnline()){
            loadManagerOrdersFirebase()?.forEach {
                db.managerOrdersDao().insert(it)
            }
        }

        return db.managerOrdersDao().loadAll()
    }

    private suspend fun loadManagerOrdersFirebase():List<ManagerOrder>?{
        return suspendCoroutine{continuation ->
            FirebaseDatabase.getInstance().getReference("manager_orders").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   val resultList = ArrayList<ManagerOrder>()
                    for(order in snapshot.children){
                        resultList.add(order.getValue(ManagerOrder::class.java)!!)
                    }
                    continuation.resume(resultList)
                }

                override fun onCancelled(error: DatabaseError) {
                   continuation.resume(null)
                }

            })
        }
    }
    suspend fun loadOrders():List<Order>{
        if(isOnline()){
            loadOrdersFirebase()?.forEach{
                db.ordersDao().insert(it)
            }
        }

        return db.ordersDao().loadAll().sortedBy { it.createTime }
    }

    private suspend fun loadOrdersFirebase() :List<Order>?{
        return suspendCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference("orders")
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val resultList = ArrayList<Order>()
                        for (order in snapshot.children) {
                            resultList.add(order.getValue(Order::class.java)!!)
                        }
                        continuation.resume(resultList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                     continuation.resume(null)
                    }

                })
        }
    }
    suspend fun loadUsers():List<User>{
        if(isOnline()){
            loadUsersFirebase()?.forEach {
                db.userDao().insert(it)
            }
        }

        return db.userDao().loadAll()
    }
    private suspend fun loadUsersFirebase(): List<User>? {
        return suspendCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val resultList = ArrayList<User>()
                        for (user in snapshot.children) {
                            resultList.add(user.getValue(User::class.java)!!)
                        }
                        continuation.resume(resultList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log(error.message)
                        continuation.resume(null)
                    }
                })
        }
    }

}