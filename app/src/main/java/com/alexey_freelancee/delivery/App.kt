package com.alexey_freelancee.delivery

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.database.AppDatabase
import com.alexey_freelancee.delivery.data.network.RetrofitClient
import com.alexey_freelancee.delivery.ui.customer_create_order.CustomerCreateOrderViewModel
import com.alexey_freelancee.delivery.ui.login.LoginViewModel
import com.alexey_freelancee.delivery.ui.main_screen.MainScreenViewModel
import com.alexey_freelancee.delivery.ui.manager_create_order.ManagerCreateOrderViewModel
import com.alexey_freelancee.delivery.ui.current_order.CurrentOrderViewModel
import com.alexey_freelancee.delivery.ui.registration.RegistrationViewModel
import com.alexey_freelancee.delivery.ui.splash_screen.SplashViewModel
import com.alexey_freelancee.delivery.utils.SharedPrefsUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.math.sign

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}

val appModule = module {
    single {
        Room
            .databaseBuilder(androidContext(), AppDatabase::class.java, "delivery_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single{ ContextCompat.getSystemService(androidContext(), LocationManager::class.java) as LocationManager }
    single { SharedPrefsUtil(androidContext()) }
    single { RetrofitClient }
    single { Repository(get(), get(),get(),get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { MainScreenViewModel(get()) }
    viewModel { CustomerCreateOrderViewModel(get())}
    viewModel { ManagerCreateOrderViewModel(get()) }
    viewModel { CurrentOrderViewModel(get(),androidContext()) }
}