package com.example.dental

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface DentalApi {
    @POST("api/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): ApiResponse

    @POST("api/book-appointment")
    suspend fun bookAppointment(@Body request: BookingRequest): ApiResponse

    @GET("api/appointments") // New endpoint to fetch the list
    suspend fun getAppointments(): List<BookingRequest>
}