package com.alexey_freelancee.delivery.ui.current_order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CurrentOrderViewModel(private val repository: Repository) : ViewModel(),OnMapReadyCallback {
    val order = MutableLiveData<ManagerOrder>()
    val subOrders = MutableLiveData<List<Order>>()
    val dataLoading = MutableLiveData<Boolean>()


    fun loadOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            dataLoading.postValue(true)
            log("loading order with create time = $createTime")
            loadSubOrders(loadManagerOrder()?.subOrders)
            dataLoading.postValue(false)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        //todo draw route
    }
    private suspend fun loadManagerOrder() :ManagerOrder? {

        val managerOrder = repository.loadManagerOrderByTime(createTime)
        if (managerOrder != null) {
            order.postValue(managerOrder!!)
        }
        return managerOrder
    }

    private suspend fun loadSubOrders(subOrdersCreateTime: List<Long>?) {
        val resultList = ArrayList<Order>()
        subOrdersCreateTime?.forEach {
            val order = repository.loadOrderByTime(it)
            if (order != null) resultList.add(order)
        }
        subOrders.postValue(resultList)
    }

    fun updateOrderStatus(status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val orderTemp = order.value?.apply {
                this.status = status
            }
            if (orderTemp != null) repository.createManagerOrder(orderTemp)
        }
    }


    fun updateSubOrderStatus(createTime: Long, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val orderTemp = repository.loadOrderByTime(createTime).apply {
                this?.status = status
            }
            if (orderTemp != null) repository.createOrder(orderTemp)
        }
    }
}