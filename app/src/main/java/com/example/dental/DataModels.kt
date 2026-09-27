package com.example.dental

// matches my /api/send-otp body
data class OtpRequest(val phone: String)

// matches my /api/book-appointment body
data class BookingRequest(
    val id: String? = null,
    val fullname: String,
    val service: String,
    val date: String,
    val time: String,
    val phone: String,
    val otp: String,
    val createdAt: String? = null
)

// matches my success response
data class ApiResponse(val success: Boolean, val message: String)