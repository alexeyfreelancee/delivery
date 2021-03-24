package com.alexey_freelancee.delivery.ui.customer_create_order

import android.app.DatePickerDialog
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.Event
import com.alexey_freelancee.delivery.utils.STATUS_PACKED
import com.alexey_freelancee.delivery.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CustomerCreateOrderViewModel(private val repository: Repository) : ViewModel() {
    val storageAddress = MutableLiveData<String>()
    val arriveTime = MutableLiveData<Long>(0)
    val dateText = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val customerName = MutableLiveData<String>()
    val customerPhone = MutableLiveData<String>()
    val comment = MutableLiveData<String>()

    val createOrder = MutableLiveData<Event<Boolean>>()
    val toast = MutableLiveData<Event<String>>()

    private val myCalendar = Calendar.getInstance()


    fun createOrder(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            if (checkInput() && isOnline()) {
                val orders = repository.loadOrders()
                val index = if(orders.isEmpty()) 1 else orders.last().index + 1
                val newOrder = Order(
                    storageAddress = storageAddress.value!!,
                    estimateTime = arriveTime.value!!,
                    index = index,
                    weight = weight.value!!.toDouble(),
                    customerName = customerName.value!!,
                    customerPhone = customerPhone.value!!,
                    status = STATUS_PACKED,
                    comment = comment.value ?: "",
                    createTime = System.currentTimeMillis()
                )
                createOrder.postValue(Event(repository.createOrder(newOrder)))
            }


        }
    }

    fun showDatePicker(view: View) {

        DatePickerDialog(
            view.context,
            { view, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val sdf = SimpleDateFormat("dd.MM HH:ss", Locale.getDefault())
                arriveTime.postValue(myCalendar.time.time)


                dateText.postValue(sdf.format(myCalendar.time))
            },
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun checkInput(): Boolean {
        val validDate = arriveTime.value != null && arriveTime.value!! != 0L && !dateText.value.isNullOrEmpty()
        if (!validDate) {
            toast.postValue(Event("Введите дату поставки товара"))
            return false;
        }

        val validWeight = !weight.value.isNullOrBlank() && weight.value != null && weight.value!!.isNotBlank()
        if (!validWeight) {
            toast.postValue(Event("Введите вес груза"))
            return false;
        }

        val validName =
            !customerName.value.isNullOrBlank() && customerName.value!!.split(" ").size > 2
        if (!validName) {
            toast.postValue(Event("Введите ФИО ответственного лица"))
            return false;
        }

        val validPhone = !customerPhone.value.isNullOrBlank()
        if (!validPhone) {
            toast.postValue(Event("Введите телефон ответственного лица"))
            return false;
        }
        return true
    }
}