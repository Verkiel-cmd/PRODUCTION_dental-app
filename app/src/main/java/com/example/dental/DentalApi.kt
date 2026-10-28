package com.example.dental

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface DentalApi {

    // Inside your existing: interface DentalApi { ... }

    // --- NEW: Add these two lines at the bottom of the interface ---
    // IMPORTANT: Change these paths to match your actual Web API routes!
    @POST("/api/login.php")
    suspend fun loginUser(@Body request: LoginRequest): AuthResponse

    @POST("/api/register.php")
    suspend fun registerUser(@Body request: RegisterRequest): AuthResponse


    @POST("api/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): ApiResponse

    @POST("api/book-appointment")
    suspend fun bookAppointment(@Body request: BookingRequest): ApiResponse

    @GET("api/appointments") // New endpoint to fetch the list
    suspend fun getAppointments(): List<BookingRequest>
}