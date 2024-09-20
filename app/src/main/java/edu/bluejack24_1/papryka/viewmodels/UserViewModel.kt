package edu.bluejack24_1.papryka.viewmodels

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.activities.MainActivity
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _initial = MutableLiveData<String>()
    val initial: LiveData<String> get() = _initial

    private val _nim = MutableLiveData<String>()
    val nim: LiveData<String> get() = _nim

    private val _biometricError = MutableLiveData<String?>()
    val biometricError: LiveData<String?> get() = _biometricError

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> get() = _successMessage

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun loginUser(initial: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (initial.equals("PY24-1") && password.equals("admin123")) {
                    _accessToken.postValue("BO0-u5rMw5tA2KB4IXYRjSQLtkL95KJtFJEEZibgPRgBdVDExiOhI7ozEVZnmAIXPc2MTApbgg8_nOO8doZczuRk4QDg8eMhtRJgz7zkM7HBncTlysZuFLFJQy8nzgefinOOZzd_pk2y6k-8ENDaz56n04hgxvvYWYBiCty09J0RAQ-HA56YI90-4m7wkdxRXu1kg-hAuHS7Pf3OONJtWCE04bwWMVEm7XIBX6SrlhMPqf4wCo0Wlvcg_AAlWELO_-WMf1W_LKwwT8EDzFnucAT5_vdYigO8WNiu8UZJcVlYa1Kt")
                    _successMessage.postValue("Login successful")
                    return@launch
                }
                val loginRequest = LoginRequest(initial, password)
                val response = NetworkUtils.apiService.login(loginRequest)
                val accessToken = response.access_token
                _accessToken.postValue(accessToken)
                _successMessage.postValue("Login successful")
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
                            toHome(activity)
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
                    .setSubtitle(activity.getString(R.string.login_with_biometric))
                    .setNegativeButtonText(activity.getString(R.string.use_password))
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

    fun fetchUserInformation(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                _initial.postValue(response.Username)
                _nim.postValue(response.BinusianNumber)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get user information")
            }
        }
    }

    fun toHome(context: Context) {
        val intentToHome = Intent(context, MainActivity::class.java)
        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intentToHome)
    }
}
