package com.alexey_freelancee.delivery.ui.manager_create_order

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.Event
import com.alexey_freelancee.delivery.utils.STATUS_COMPLETED
import com.alexey_freelancee.delivery.utils.STATUS_PACKED
import com.alexey_freelancee.delivery.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

class ManagerCreateOrderViewModel(private val repository: Repository) : ViewModel() {
    val estimateTime = MutableLiveData<Long>()
    val estimateTextTime = MutableLiveData<String>()
    val subOrders = MutableLiveData<List<Long>>()
    val subOrdersFull = MutableLiveData<List<Order>>()
    val availableOrders = MutableLiveData<List<Order>>()
    val weight = MutableLiveData<Double>()

    val createOrder = MutableLiveData<Event<Boolean>>()
    val toast = MutableLiveData<Event<String>>()


    fun createOrder(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            if (checkInput() && isOnline()) {
                val order = ManagerOrder(
                    status = STATUS_PACKED,
                    weight = weight.value!!,
                    createTime = System.currentTimeMillis(),
                    estimateTime = estimateTime.value!!,
                    subOrders = subOrders.value!!
                )
               createOrder.postValue(Event(repository.createManagerOrder(order)))
            }
        }

    }

    private fun checkInput(): Boolean {
        if (subOrders.value.isNullOrEmpty()) {
            toast.value = Event("Выберите хотя бы один заказ")
            return false
        }

        return true
    }

    fun addSubOrder(order: Order) {
        CoroutineScope(Dispatchers.IO).launch {
            val subOrdersTemp = ArrayList(subOrders.value ?: emptyList())
            val subOrdersFullTemp = ArrayList(subOrdersFull.value ?: emptyList())

            subOrdersFullTemp.add(order)
            subOrdersTemp.add(order.createTime)

            val weight = DecimalFormat("##.##").format(subOrdersFullTemp.sumOf { it.weight }).toDouble()
            this@ManagerCreateOrderViewModel.weight.postValue(weight)

            subOrders.postValue(subOrdersTemp)
            subOrdersFull.postValue(subOrdersFullTemp)
        }
    }


    fun loadAvailableOrders(estimateTime: String, estimateTextTime: String) {
        CoroutineScope(Dispatchers.IO).launch {
            this@ManagerCreateOrderViewModel.estimateTime.postValue(estimateTime.toLong())
            this@ManagerCreateOrderViewModel.estimateTextTime.postValue(estimateTextTime)

            val availableOrders = repository
                .loadOrders()
                .filter { it.status != STATUS_COMPLETED && it.estimateTime <= estimateTime.toLong() }
                .sortedByDescending { it.createTime }
            this@ManagerCreateOrderViewModel.availableOrders.postValue(availableOrders)


        }
    }
}