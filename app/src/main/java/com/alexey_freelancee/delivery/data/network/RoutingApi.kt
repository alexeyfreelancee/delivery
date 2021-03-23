package com.alexey_freelancee.delivery.data.network

import com.alexey_freelancee.delivery.data.models.route.RouteResponse
import com.alexey_freelancee.delivery.utils.ROUTING_API_KEY
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutingApi {

    @GET("/routing/1/calculateRoute/{curLat},{curLon}:{destLat},{destLong}/json")
    suspend fun loadRoute(

        @Path("curLat") curLat:Double,
        @Path("curLon") curLong:Double,
        @Path("destLat") destLat:Double,
        @Path("destLong") destLong:Double,
        @Query("key") key: String = ROUTING_API_KEY
    ): RouteResponse
}