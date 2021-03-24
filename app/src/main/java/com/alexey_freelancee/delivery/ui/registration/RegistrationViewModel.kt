package com.alexey_freelancee.delivery.ui.registration

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.User
import com.alexey_freelancee.delivery.utils.Event
import com.alexey_freelancee.delivery.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RegistrationViewModel(private val repository: Repository) : ViewModel() {
    val type = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val password = MutableLiveData<String>()



    val doRegistration = MutableLiveData<Event<String>>()

    fun doRegistration(view:View){
        CoroutineScope(Dispatchers.IO).launch {
            if(checkInput()){
                log("check input success")
                val user = User(
                    uid = UUID.randomUUID().toString(),
                    email = email.value!!,
                    phoneNumber = phone.value!!,
                    fullName = name.value!!,
                    type = type.value!!
                    )
                val result = repository.doRegister(user, password.value!!)
                if(result == "ok"){
                    repository.setUserId(user.uid)
                }
                doRegistration.postValue(Event(result))
            }

        }
    }


    private fun checkInput() :Boolean{


        val validEmail = !email.value.isNullOrEmpty()
        if(!validEmail){
            doRegistration.postValue(Event("Введите корректный email"))
            return false
        }

        val validPhone = !phone.value.isNullOrEmpty()
        if(!validPhone){
            doRegistration.postValue(Event("Введите номер телефона"))
            return false
        }

        val validName = !name.value.isNullOrBlank() && name.value!!.split(" ").size > 2
        if(!validName){
            doRegistration.postValue(Event("Введите ФИО"))
            return false
        }

        val validPassword = !password.value.isNullOrBlank() && password.value!!.length >= 6
        if(!validPassword){
            doRegistration.postValue(Event("Введите пароль длиной минимум в 6 символов"))
            return false
        }

        return true
    }
}