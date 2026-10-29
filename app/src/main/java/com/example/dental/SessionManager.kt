package com.example.dental

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DentalAppPrefs", Context.MODE_PRIVATE)

    fun saveAuth(token: String, role: String) {
        prefs.edit()
            .putString("USER_TOKEN", token)
            .putString("USER_ROLE", role)
            .apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("USER_TOKEN", null)
    }

    fun fetchUserRole(): String? {
        return prefs.getString("USER_ROLE", "patient") // Defaults to patient
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}