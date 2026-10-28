package com.example.dental

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DentalAppPrefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("USER_TOKEN", token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("USER_TOKEN", null)
    }

    fun logout() {
        prefs.edit().remove("USER_TOKEN").apply()
    }
}
