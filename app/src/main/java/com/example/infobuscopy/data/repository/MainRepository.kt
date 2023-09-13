package com.example.infobuscopy.data.repository

import android.util.Log
import com.example.infobuscopy.data.model.BusRouteModel
import com.example.infobuscopy.data.network.InfoBusService

class MainRepository(
    private val service: InfoBusService,
) {

    suspend fun get14BusRoutes(): List<BusRouteModel>? {
        return try {
            service.get14BusRoutes().apply {
                Log.e(TAG, "get14BusRoutes: $this")
            }
        } catch (e: Exception) {
            Log.e(TAG, "get14BusRoutes CATCH: ${e.printStackTrace()}")
            null
        }
    }

    companion object {
        private const val TAG = "MainRepository"
    }
}