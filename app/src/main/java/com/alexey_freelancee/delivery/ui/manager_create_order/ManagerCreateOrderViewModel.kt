package com.alexey_freelancee.delivery.ui.manager_create_order

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class ManagerCreateOrderViewModel(private val repository: Repository) : ViewModel() {
    val time = MutableLiveData<Long>()
    val testTime = MutableLiveData<String>()
    val subOrders = MutableLiveData<List<Long>>()
    val subOrdersFull = MutableLiveData<List<Order>>()
    val availableOrders = MutableLiveData<List<Order>>()
    val weight = MutableLiveData<Double>()

    val createOrder = MutableLiveData<Event<String>>()
    val toast = MutableLiveData<Event<String>>()


    fun createOrder(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (checkInput()) {
                    if(isOnline()){
                        val order = ManagerOrder(
                            status = STATUS_PACKED,
                            weight = weight.value ?: 0.0,
                            createTime = System.currentTimeMillis(),
                            estimateTime = time.value ?: 0,
                            subOrders = subOrders.value ?: emptyList()
                        )
                        log("creating order")
                        val result = repository.createManagerOrder(order)
                        log("posted value")
                        createOrder.postValue(Event(result))
                    }else{
                        createOrder.postValue(Event("Нет подключения к интернету"))
                    }

                }
            }catch (ex:Exception){
                createOrder.postValue(Event(ex.message ?: "Ошибка"))
            }

        }

    }

    private fun checkInput(): Boolean {
        if (subOrders.value.isNullOrEmpty()) {
            toast.postValue(Event("Выберите хотя бы один заказ"))
            return false
        }

        return true
    }

    fun addSubOrder(order: Order) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val subOrdersTemp = ArrayList(subOrders.value ?: emptyList())

                val subOrdersFullTemp = ArrayList(subOrdersFull.value ?: emptyList())
                if(subOrdersFullTemp.find { it.createTime == order.createTime } == null){
                    subOrdersFullTemp.add(order)
                    subOrdersTemp.add(order.createTime)

                    val weightSum = subOrdersFullTemp.sumOf { it.weight }
                    val roundedWeightSum = (weightSum * 100.0).roundToInt() / 100.0
                    this@ManagerCreateOrderViewModel.weight.postValue(roundedWeightSum)


                    subOrders.postValue(subOrdersTemp)
                    subOrdersFull.postValue(subOrdersFullTemp)
                }
            }catch (ex:java.lang.Exception){
                toast.postValue(Event(ex.message ?: "Ошибка"))
            }


        }
    }


    fun loadAvailableOrders(estimateTime: String, estimateTextTime: String) {
        CoroutineScope(Dispatchers.IO).launch {
            this@ManagerCreateOrderViewModel.time.postValue(estimateTime.toLong())
            this@ManagerCreateOrderViewModel.testTime.postValue(estimateTextTime)

            val managedIds = HashSet<Long>()
            repository.loadManagerOrders().forEach { managedIds.addAll(it.subOrders) }
            val resultList = ArrayList<Order>()


            repository
                .loadOrders()
                .filter {
                    val orderDate = Calendar.getInstance().apply {
                        time = Date(it.estimateTime)
                    }
                    val pickedDate = Calendar.getInstance().apply {
                        time = Date(estimateTime.toLong() - 1000 * 60 * 60 * 12)
                    }

                    it.status != STATUS_COMPLETED && pickedDate <= orderDate
                }
                .sortedBy { it.estimateTime }
                .forEach { if(!managedIds.contains(it.createTime)) resultList.add(it)}

            this@ManagerCreateOrderViewModel.availableOrders.postValue(resultList)


        }
    }
}