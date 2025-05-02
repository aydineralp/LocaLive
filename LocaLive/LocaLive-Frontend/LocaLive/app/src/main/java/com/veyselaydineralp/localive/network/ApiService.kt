package com.veyselaydineralp.localive.network

import com.veyselaydineralp.localive.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/Location")
    fun getAllLocations(): Call<Response<List<Location>>>

    @GET("api/Location/{id}")
    fun getLocationById(@Path("id") id: Int): Call<ApiResponse<Location>>

    @POST("api/Location")
    fun addLocation(@Body location: Location): Call<ApiResponse<Location>>

    @PUT("api/Location/{id}")
    fun updateLocation(@Path("id") id: Int, @Body location: Location): Call<ApiResponse<Void>>


    @DELETE("api/Location/{id}")
    fun deleteLocation(@Path("id") id: Int): Call<Void>

    @GET("api/Trip/{userId}")
    fun getTripsByUserId(
        @Path("userId") userId: Int
    ): Call<List<Event>>

    @GET("api/Event/fetch-and-recommend/{userId}")
    fun getUnifiedProcess(
        @Path("userId") userId: Int,
        @Query("startDateTime") startDate: String,
        @Query("endDateTime") endDate: String
    ): Call<Void>

    @GET("api/Event")
    fun getAllEvents(): Call<Response<List<Event>>>

    @POST("api/Event")
    fun addEvent(@Body event: Event): Call<Response<Event>>

    @POST("api/User")
    fun AddUser(@Body registerRequest: RegisterRequest): Call<Void>

    @POST("api/User/Login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api/User/{id}")
    fun getUserById(@Path("id") id: Int): Call<Map<String, Any>>

    @PATCH("api/User/{id}/preferences")
    fun updatePreferences(
        @Path("id") userId: Int,
        @Body preferences: String
    ): Call<Void>



}