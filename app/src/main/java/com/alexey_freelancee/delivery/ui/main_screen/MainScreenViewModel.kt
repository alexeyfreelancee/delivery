package com.alexey_freelancee.delivery.ui.main_screen

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.Event
import com.alexey_freelancee.delivery.utils.STATUS_COMPLETED
import com.alexey_freelancee.delivery.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class MainScreenViewModel(
    private val repository: Repository
) : ViewModel() {
    val customerOrdersFresh = MutableLiveData<List<Order>>()
    val customerOrdersHistory = MutableLiveData<List<Order>>()


    val managerOrdersFresh = MutableLiveData<List<Order>>()
    val managerOrdersHistory = MutableLiveData<List<ManagerOrder>>()

    val supplierOrdersFresh = MutableLiveData<List<ManagerOrder>>()
    val supplierOrdersHistory = MutableLiveData<List<ManagerOrder>>()

    val updating = MutableLiveData<Boolean>()
    val dataLoading = MutableLiveData<Boolean>()
    val userType = MutableLiveData<String>()

    private val myCalendar = Calendar.getInstance()
    val toast = MutableLiveData<Event<String>>()
    val createManagerOrder = MutableLiveData<Event<Bundle>>()



    suspend fun updateSupplierHistory() = withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadManagerOrders().filter { it.status == STATUS_COMPLETED }.sortedByDescending { it.createTime }
        supplierOrdersHistory.postValue(orders)
    }


   suspend   fun updateSupplierFresh()= withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadManagerOrders().filter { it.status != STATUS_COMPLETED }.sortedByDescending { it.createTime }
        supplierOrdersFresh.postValue(orders)
    }


    suspend fun updateCustomerFresh()= withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadOrders()
        customerOrdersFresh.postValue(orders.filter { it.status != STATUS_COMPLETED }.sortedByDescending { it.createTime })
    }

    suspend fun updateCustomerHistory()= withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadOrders()
        customerOrdersHistory.postValue(orders.filter { it.status == STATUS_COMPLETED }.sortedByDescending { it.createTime })
    }

    suspend fun updateManagerFresh()= withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadOrders()
        val managedIds = HashSet<Long>()
        repository.loadManagerOrders().forEach { managedIds.addAll(it.subOrders) }
        val resultList = ArrayList<Order>()
        orders.forEach { if(!managedIds.contains(it.createTime)) resultList.add(it)}
        managerOrdersFresh.postValue(resultList.sortedByDescending { it.createTime })
    }



    suspend fun updateManagerHistory()= withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val orders = repository.loadManagerOrders()
        managerOrdersHistory.postValue(orders.sortedByDescending { it.createTime })
    }

    fun updateUserType(){
        CoroutineScope(Dispatchers.IO).launch {
            dataLoading.postValue(true)
            userType.postValue(repository.loadUserByUid(repository.loadUid())?.type)
            dataLoading.postValue(false)
        }
    }

    suspend fun loadSubOrders(subOrderCreateTime:List<Long>) :List<Order>{
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
            val resultList = ArrayList<Order>()
            subOrderCreateTime.forEach {
                val order = repository.loadOrderByTime(it)
                if(order!=null) resultList.add(order)
            }
            resultList
        }

    }
    fun showManagerDatePicker(context:Context){
        DatePickerDialog(
            context,
            { view, year, month, dayOfMonth ->
                CoroutineScope(Dispatchers.IO).launch {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    val sdf = SimpleDateFormat("dd.MM HH:ss", Locale.getDefault())
                    val bundle = Bundle().apply{

                        putString("estimatedTime", myCalendar.time.time.toString())
                        putString("estimatedTimeText", sdf.format(myCalendar.time) )
                    }
                    val managedIds = HashSet<Long>()
                    repository.loadManagerOrders().forEach { managedIds.addAll(it.subOrders) }
                    val resultList = ArrayList<Order>()
                    repository
                        .loadOrders()
                        .filter { it.status != STATUS_COMPLETED && it.estimateTime <=  myCalendar.time.time.toLong() }
                        .sortedBy { it.estimateTime }
                        .forEach { if(!managedIds.contains(it.createTime)) resultList.add(it)}

                    if(resultList.isEmpty()){
                        toast.postValue(Event("Нет доступных заказов"))
                    }else{
                        createManagerOrder.postValue(Event(bundle))
                    }

                }
            },
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}