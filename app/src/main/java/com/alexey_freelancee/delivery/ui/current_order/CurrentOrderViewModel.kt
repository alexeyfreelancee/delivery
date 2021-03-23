package com.alexey_freelancee.delivery.ui.current_order

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexey_freelancee.delivery.data.Repository
import com.alexey_freelancee.delivery.data.models.GoogleMapDTO
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.SortPlaces
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CurrentOrderViewModel(private val repository: Repository) : ViewModel(),OnMapReadyCallback {
    val order = MutableLiveData<ManagerOrder>()
    val subOrders = MutableLiveData<List<Order>>()
    val dataLoading = MutableLiveData<Boolean>()
    val sortedDestinations = MutableLiveData<List<LatLng>>()
    private val storageCoordinates = hashMapOf(
        "55.833410,49.050410" to "Восстания, 100 к8100, Московский район, Казань",
        "55.853660,49.024070" to "Тэцевская, 191, Московский район, Казань",
        "54.222790,49.598610" to "Ганенкова, 55 к3, Димитровград, Ульяновская область",
        "55.916640,49.151780" to "Магистральная, 4, Приволжский район, Казань",
        "55.646490,49.214330" to "Советская, 271, с. Столбище, Лаишевский район, Республика Татарстан",
        "53.255020,50.391370" to "Механиков, 2, пгт Смышляевка, Волжский район, Самарская область",
        "56.805677,53.200946" to "Пойма, 19Б, Ленинский район, Ижевск",
        "56.105340,54.252380" to "Техснабовская, 5, Нефтекамск, Республика Башкортостан",
        "53.653560,55.966730" to "Профсоюзная, 16 к2, Восточный район, Стерлитамак, Республика Башкортостан",
        "54.763630,55.484050" to "Совхозная, 15л, Северная поляна, Октябрьский район, Пенза",
        "55.669300,52.341560" to "Старосармановская, 29, Комсомольский район, Набережные Челны",
        "53.093270,46.572510" to "Алексеевское Шоссе, 5, Кузнецк, Пензенская область",
        "55.601190,51.859400" to "Промышленная 4, 16а ст1, Нижнекамск",
        "56.098620,47.514780" to "Промышленная улица, 40а, Восточный район, Новочебоксарск, Чувашская Республика — Чувашия",
        "56.065840,47.270690" to "Канашское шоссе, 7/1 к7, Чебоксары",
    )

    override fun onMapReady(map: GoogleMap) {
        CoroutineScope(Dispatchers.IO).launch {

            val current = repository.loadCurrentDestination()
            withContext(Dispatchers.Main){
                map.addMarker(MarkerOptions().position(current))
                map.moveCamera(CameraUpdateFactory.newLatLng(current))
               // map.uiSettings.isZoomGesturesEnabled = false;
            }


            val destinations = ArrayList(sortedDestinations.value ?: emptyList())
            for((i,destination) in destinations.withIndex()){
                val polyline =    if(i==0){
                   getPolyline(current,destination)
                }else{
                    getPolyline(destinations[i-1], destination)

                }
                withContext(Dispatchers.Main){
                    map.addMarker(MarkerOptions().position(destination))
                    map.addPolyline(polyline)
                }

            }

        }

    }

    private suspend fun getPolyline(current:LatLng, destination:LatLng) : PolylineOptions{
        val url = getURL(current, destination)
        val directions = Gson().fromJson(repository.requestDirection(url) ?: "",GoogleMapDTO::class.java)




        val result =  ArrayList<List<LatLng>>()
        val path =  ArrayList<LatLng>()

        for (i in 0 until directions.routes[0].legs[0].steps.size){

            path.addAll(decodePolyline(directions.routes[0].legs[0].steps[i].polyline.points))
        }
        result.add(path)
        val polyline = PolylineOptions()
        for (i in result.indices){
            polyline.addAll(result[i])
            polyline.width(7f)
            polyline.color(Color.GREEN)
            polyline.geodesic(true)
        }
        return polyline
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    fun loadData() = viewModelScope.launch{
        dataLoading.postValue(true)
        val managerOrder =loadManagerOrder(createTime)
        if(managerOrder!=null) {
            order.postValue(managerOrder!!)
            val subOrdersNew = loadSubOrders(managerOrder.subOrders)
            subOrders.postValue(subOrdersNew)
            val sortedDestinationsNew = loadSortedDestinations(subOrdersNew)
            sortedDestinations.postValue(sortedDestinationsNew)
        }

        dataLoading.postValue(false)
    }



    private suspend fun loadSortedDestinations(orders:List<Order>):List<LatLng> = withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        val destinations = ArrayList<LatLng>()
        orders.forEach {
            val location = LatLng(
                storageCoordinates[it.storageAddress]!!.split(",")[0].toDouble(),
                storageCoordinates[it.storageAddress]!!.split(",")[1].toDouble()
            )
            destinations.add(location)
        }
        Collections.sort(destinations, SortPlaces(repository.loadCurrentDestination()))
        destinations
    }






    private suspend fun loadManagerOrder(createTime: Long) : ManagerOrder? = withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
        repository.loadManagerOrderByTime(com.alexey_freelancee.delivery.ui.current_order.createTime)
    }

    private suspend fun loadSubOrders(subOrdersCreateTime: List<Long>?) = withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
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
}