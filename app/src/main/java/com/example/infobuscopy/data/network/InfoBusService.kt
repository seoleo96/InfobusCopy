package com.example.infobuscopy.data.network

import com.example.infobuscopy.data.model.BusRouteModel
import retrofit2.http.GET
import retrofit2.http.Path

interface InfoBusService {


    @GET("{idBus}/busses")
    suspend fun get14BusRoutes(
       @Path("idBus") routeId : Int
    ): List<BusRouteModel>
}