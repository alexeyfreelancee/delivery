package com.alexey_freelancee.delivery.ui.splash_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexey_freelancee.delivery.data.Repository
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository) : ViewModel(){



    fun checkLogin() = repository.checkLogin()
}