package edu.bluejack24_1.papryka.utils

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "AppPreference"
    private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"

    // Function to get the access token
    fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    // Function to save the access token (for login scenarios)
    fun saveAccessToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(ACCESS_TOKEN_KEY, token)
            apply()
        }
    }

    // Function to clear the access token (for logout scenarios)
    fun clearAccessToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN_KEY)
            apply()
        }
    }
}
