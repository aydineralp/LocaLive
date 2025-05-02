package com.veyselaydineralp.localive.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("tripId") val tripId: Int,
    @SerializedName("tripName") val eventName: String,
    @SerializedName("tripDescription") val eventDescription: String,
    @SerializedName("tripUrl") val tripUrl: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("tripDate") val eventDate: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("requestGroupId") val requestGroupId: Int,
    @SerializedName("tripComment") val TripComment: String
)
