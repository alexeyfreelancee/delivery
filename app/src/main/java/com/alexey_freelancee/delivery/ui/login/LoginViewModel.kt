package com.alexey_freelancee.delivery.ui.login


import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.utils.Event
import com.alexey_freelancee.delivery.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: Repository
) : ViewModel() {
    val email = MutableLiveData<String>()
    val pass = MutableLiveData<String>()
    val successLogin = MutableLiveData<Event<Boolean>>()
    val doRegistration = MutableLiveData<Event<Boolean>>()

    fun doRegistration(view:View){
        doRegistration.postValue(Event(true))
    }
    fun doLogin(view:View){
        CoroutineScope(Dispatchers.IO).launch {
            if(isOnline()){
                val user = repository.loadUserByEmail(email.value)
                if(user!=null){
                    if(repository.doLogin(user.uid, email.value, pass.value)){
                        repository.setUserId(user.uid)
                        successLogin.postValue(Event(true))
                    }
                }
            }
            successLogin.postValue(Event(true))
        }
    }
}