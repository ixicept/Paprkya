package edu.bluejack24_1.papryka.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.papryka.activities.LoginActivity

class SettingsViewModel : ViewModel() {

    private val _selectedLanguage = MutableLiveData<String>()
    val selectedLanguage: LiveData<String> get() = _selectedLanguage

    fun setSelectedLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
    }

    fun getCurrentLanguage(context: Context): String {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("selected_language", "en") ?: "en"
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_language", languageCode)
            apply()
        }
    }

    fun logout(context: Context) {
        val intentToLogin = Intent(context, LoginActivity::class.java)
        intentToLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intentToLogin)
    }
}
