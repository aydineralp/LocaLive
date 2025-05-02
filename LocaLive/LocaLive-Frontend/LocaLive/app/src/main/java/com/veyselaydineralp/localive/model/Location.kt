package com.veyselaydineralp.localive.model

data class Location(
    val LocationId: Int = 0,
    val Country: String,
    val City: String,
    val Latitude: Double,
    val Longitude: Double,
    val isAutoDetected: Boolean
)
