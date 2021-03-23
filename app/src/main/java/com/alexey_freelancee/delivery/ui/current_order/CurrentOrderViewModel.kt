package com.alexey_freelancee.delivery.ui.current_order

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.Repository

import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.SortPlaces
import com.alexey_freelancee.delivery.utils.isOnline
import com.alexey_freelancee.delivery.utils.log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CurrentOrderViewModel(private val repository: Repository, private val context:Context) : ViewModel(), OnMapReadyCallback {
    val order = MutableLiveData<ManagerOrder>()
    val subOrders = MutableLiveData<List<Order>>()
    val dataLoading = MutableLiveData<Boolean>()
    val sortedDestinations = MutableLiveData<List<LatLng>>()
    private var current : LatLng? = null

    private val storageCoordinates = hashMapOf(
        "Восстания, 100 к8100, Московский район, Казань" to "55.833410,49.050410",
        "Тэцевская, 191, Московский район, Казань" to "55.853660,49.024070",
        "Ганенкова, 55 к3, Димитровград, Ульяновская область" to "54.222790,49.598610",
        "Магистральная, 4, Приволжский район, Казань" to "55.916640,49.151780",
        "Советская, 271, с. Столбище, Лаишевский район, Республика Татарстан" to "55.646490,49.214330",
        "Механиков, 2, пгт Смышляевка, Волжский район, Самарская область" to "53.255020,50.391370",
        "Пойма, 19Б, Ленинский район, Ижевск" to "56.805677,53.200946",
        "Техснабовская, 5, Нефтекамск, Республика Башкортостан" to "56.105340,54.252380",
        "Профсоюзная, 16 к2, Восточный район, Стерлитамак, Республика Башкортостан" to "53.653560,55.966730",
        "Совхозная, 15л, Северная поляна, Октябрьский район, Пенза" to "54.763630,55.484050",
        "Старосармановская, 29, Комсомольский район, Набережные Челны" to "55.669300,52.341560",
        "Алексеевское Шоссе, 5, Кузнецк, Пензенская область" to "53.093270,46.572510",
        "Промышленная 4, 16а ст1, Нижнекамск" to "55.601190,51.859400",
        "Промышленная улица, 40а, Восточный район, Новочебоксарск, Чувашская Республика — Чувашия" to "56.098620,47.514780",
        "Канашское шоссе, 7/1 к7, Чебоксары" to "56.065840,47.270690",
    )

    fun loadData(fromOrderInfo:Boolean) = viewModelScope.launch {
        dataLoading.postValue(true)
        val managerOrder = loadManagerOrder(createTime)
        if (managerOrder != null) {
            order.postValue(managerOrder!!)
            val subOrdersNew = loadSubOrders(managerOrder.subOrders)
            subOrders.postValue(subOrdersNew)
            if(!fromOrderInfo){
                if(isOnline()){
                    loadCurrentDestination()
                } else{
                    dataLoading.postValue(false)
                }
            }else{
                dataLoading.postValue(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun loadCurrentDestination(){
        val locationManager: LocationManager = context.getSystemService(LocationManager::class.java)
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 0, 0f, object: LocationListener {

                override fun onLocationChanged(location: Location) {
                    viewModelScope.launch {
                        log("received location")
                        val managerOrder = loadManagerOrder(createTime)
                        if (managerOrder != null) {
                            val subOrdersNew = loadSubOrders(managerOrder.subOrders)
                            current = LatLng(location.latitude,location.longitude)
                            val sortedDestinationsNew = loadSortedDestinations(subOrdersNew)
                            sortedDestinations.postValue(sortedDestinationsNew)
                        }
                    }
                    locationManager.removeUpdates(this)
                }

            })
    }


    override fun onMapReady(map: GoogleMap) {
        CoroutineScope(Dispatchers.IO).launch {

            val destinations = ArrayList(sortedDestinations.value ?: emptyList())
            withContext(Dispatchers.Main) {
                val currentPolyline = getPolyline(current!!, destinations[0])
                map.addMarker(MarkerOptions().position(current!!))
                map.addPolyline(currentPolyline)
                for ((i, item) in destinations.withIndex()) {

                    map.addMarker(MarkerOptions().position(item))
                    try {
                        val polyline= getPolyline(item, destinations[i + 1])
                        map.addPolyline(polyline)
                    }catch (ex:IndexOutOfBoundsException){

                    }
                }
                log("added polyline")
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,10f))

            }
            dataLoading.postValue(false)
        }

    }






    private suspend fun loadSortedDestinations(orders: List<Order>): List<LatLng> =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val destinations = ArrayList<LatLng>()
            orders.forEach {

                if (storageCoordinates[it.storageAddress] != null) {
                    val lat =  storageCoordinates[it.storageAddress]!!.split(",")[0].toDouble()
                    val lon =  storageCoordinates[it.storageAddress]!!.split(",")[1].toDouble()
                    val location = LatLng(lat,lon )
                    if(destinations.find { it.latitude == lat && it.longitude == lon } == null){
                        destinations.add(location)
                    }

                }

            }

            Collections.sort(destinations, SortPlaces(current))
            destinations
        }


    private suspend fun loadManagerOrder(createTime: Long): ManagerOrder? =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            repository.loadManagerOrderByTime(com.alexey_freelancee.delivery.ui.current_order.createTime)
        }

    private suspend fun loadSubOrders(subOrdersCreateTime: List<Long>?) =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val resultList = ArrayList<Order>()
            subOrdersCreateTime?.forEach {
                val order = repository.loadOrderByTime(it)
                if (order != null) resultList.add(order)
            }
            resultList
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

    private suspend fun getPolyline(current: LatLng, destination: LatLng): PolylineOptions? {
        return try{
            val route = repository.loadRoute(current,destination)
            val polyline = PolylineOptions()
            polyline.width(10f)
            polyline.color(context.getColor(R.color.darkGreen))
            polyline.geodesic(true)
            route.routes[0].legs[0].points.forEach {
                polyline.add(LatLng(it.latitude, it.longitude))
            }
             polyline
        }catch (ex:Exception){
            val polyline = PolylineOptions()
            polyline.width(10f)
            polyline.color(context.getColor(R.color.darkGreen))
            polyline.geodesic(true)
            polyline.add(current)
            polyline.add(destination)
            polyline
        }


    }


}