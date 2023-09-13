package com.example.infobuscopy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BusRouteModel(
    @SerialName("id")  val id: Long,
    @SerialName("cityId")  val cityId: Int,
    @SerialName("busreportRouteId")  val busreportRouteId: Int,
    @SerialName("imei")  val imei: String,
    @SerialName("name")  val name: String,
    @SerialName("direction")  val direction: Int,
    @SerialName("speed")  val speed: Int,
    @SerialName("lat")  val lat: Double,
    @SerialName("lon")  val lon: Double,
    @SerialName("invalidAdapted")  val invalidAdapted: Boolean,
    @SerialName("offline")  val offline: Boolean,
    @SerialName("filling")  val filling: Int?,
)