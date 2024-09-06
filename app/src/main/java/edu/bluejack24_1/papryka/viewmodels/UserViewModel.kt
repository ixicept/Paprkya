package edu.bluejack24_1.papryka.viewmodels

import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    private val _biometricError = MutableLiveData<String?>()
    val biometricError: LiveData<String?> get() = _biometricError

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun loginUser(initial: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(initial, password)
                val response = NetworkUtils.apiService.login(loginRequest)
                val accessToken = response.access_token
                _accessToken.postValue(accessToken)
            } catch (e: Exception) {
                _loginError.postValue("Wrong credentials")
            }
        }
    }

    fun setupBiometricPrompt(activity: AppCompatActivity) {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        _biometricError.postValue("Authentication error: $errString")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        val sharedPreferences = activity.getSharedPreferences("AppPreference", MODE_PRIVATE)
                        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
                        if (accessToken != null) {
                            fetchUserInformation(accessToken)
                        } else {
                            _biometricError.postValue("Access token not found")
                        }
                    }

                    override fun onAuthenticationFailed() {
                        _biometricError.postValue("Authentication failed")
                    }
                })

                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Login")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use password")
                    .build()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _biometricError.postValue("Biometric hardware not available")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                _biometricError.postValue("Biometric hardware currently unavailable")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _biometricError.postValue("No biometric credentials enrolled")
            }
        }
    }

    fun authenticateBiometric() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun fetchUserInformation(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                // Handle successful user info retrieval
            } catch (e: Exception) {
                _loginError.postValue("Failed to get user information")
            }
        }
    }
}
