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