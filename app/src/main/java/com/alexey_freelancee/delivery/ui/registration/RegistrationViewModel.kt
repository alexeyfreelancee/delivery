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


    val toast = MutableLiveData<Event<String>>()
    val doRegistration = MutableLiveData<Event<Boolean>>()

    fun doRegistration(view:View){
        CoroutineScope(Dispatchers.IO).launch {
            if(checkInput()){
                val user = User(
                    uid = UUID.randomUUID().toString(),
                    email = email.value!!,
                    phoneNumber = phone.value!!,
                    fullName = name.value!!,
                    type = type.value!!
                    )
                if(repository.doRegister(user, password.value!!)){
                    repository.setUserId(user.uid)
                    doRegistration.postValue(Event(true))
                }

            }
            doRegistration.postValue(Event(false))
        }
    }


    private fun checkInput() :Boolean{


        val validEmail = !TextUtils.isEmpty(email.value) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()
        if(!validEmail){
            toast.postValue(Event("Введите корректный email"))
            return false
        }

        val validPhone = !phone.value.isNullOrEmpty()
        if(!validPhone){
            toast.postValue(Event("Введите номер телефона"))
            return false
        }

        val validName = !name.value.isNullOrBlank() && name.value!!.split(" ").size > 2
        if(!validName){
            toast.postValue(Event("Введите ФИО"))
            return false
        }

        val validPassword = !password.value.isNullOrBlank() && password.value!!.length > 6
        if(!validPassword){
            toast.postValue(Event("Введите пароль длиной минимум в 6 символов"))
            return false
        }

        return true
    }
}