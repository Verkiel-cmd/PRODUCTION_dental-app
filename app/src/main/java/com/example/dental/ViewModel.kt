package com.example.dental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue      // for GET value
import androidx.compose.runtime.mutableStateOf // mutable state
import androidx.compose.runtime.setValue       // for SET value
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    // 1. This is where the app "holds" the data
    var appointments by mutableStateOf<List<BookingRequest>>(emptyList())

    // 2. The function to go get the data
    fun fetchAppointments() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.api.getAppointments()
                appointments = result // Save the result in our storage bin
            } catch (e: Exception) {
                // Handle error (e.g. no internet)
            }
        }
    }
}

class AuthViewModel : ViewModel() {

    fun loginUser(
        email: String,
        pass: String,
        sessionManager: SessionManager,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Send request to Web API using your existing RetrofitClient
                // NOTE: Change 'RetrofitClient.api' to whatever your RetrofitClient variable is named!
                val response = RetrofitClient.api.loginUser(LoginRequest(email, pass))

                if (response.success && response.token != null) {
                    sessionManager.saveAuthToken(response.token) // Save to localStorage
                    onSuccess()
                } else {
                    onError(response.message ?: "Login Failed")
                }
            } catch (e: Exception) {
                onError("Network error: Please check internet or API URL")
            }
        }
    }

    fun registerUser(
        fullName: String,
        email: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Send request to Web API using your existing RetrofitClient
                // NOTE: Change 'RetrofitClient.api' to whatever your RetrofitClient variable is named!
                val response = RetrofitClient.api.registerUser(RegisterRequest(fullName, email, pass))
                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message ?: "Registration Failed")
                }
            } catch (e: Exception) {
                onError("Network error: Please check internet or API URL")
            }
        }
    }
}