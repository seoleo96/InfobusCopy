package com.example.infobuscopy.data.network

import com.example.infobuscopy.data.model.BusRouteModel
import retrofit2.http.GET

interface InfoBusService {


    @GET("244/busses")
    suspend fun get14BusRoutes(): List<BusRouteModel>
}