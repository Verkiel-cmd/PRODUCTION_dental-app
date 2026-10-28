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

// ... (Keep your existing OtpRequest, BookingRequest, and ApiResponse up here) ...

// --- NEW AUTHENTICATION MODELS (Add these to the bottom) ---

// What you send to the Web API for Login
data class LoginRequest(val email: String, val pass: String)

// What you send to the Web API for Register
data class RegisterRequest(val fullName: String, val email: String, val pass: String)

// What the Web API sends back to you on Login/Register
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? // Your web API should return a token/userId on success
)