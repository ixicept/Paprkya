package edu.bluejack24_1.papryka.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import edu.bluejack24_1.papryka.databinding.ActivityLoginBinding
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnLogin.setOnClickListener { 
            val initial = binding.etInitial.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (initial.isNotEmpty() && password.isNotEmpty()) {

                // Initial Validation
                if (initial.indexOf('-', 0) != 4) {
                    Toast.makeText(this@LoginActivity, "Initial is not valid", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // TODO: Messier account DONE
                val loginRequest = LoginRequest(initial, password)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = NetworkUtils.apiService.login(loginRequest)
                        val accessToken = response.access_token
                        withContext(Dispatchers.Main) {
                            val sharedPreferences = getSharedPreferences("AppPreference", MODE_PRIVATE)
                            sharedPreferences.edit().putString("ACCESS_TOKEN", accessToken).apply()

//                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            val intentToHome = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intentToHome)
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Wrong credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(this@LoginActivity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val sharedPreferences = getSharedPreferences("AppPreference", MODE_PRIVATE)
                        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
                        if (accessToken != null) {
                            getUserInformation(accessToken)
                        } else {
                            Toast.makeText(this@LoginActivity, "Access token not found", Toast.LENGTH_SHORT).show()
                        }

//                        val intentToHome = Intent(this@LoginActivity, MainActivity::class.java)
//                        startActivity(intentToHome)
//                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                })

                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Login")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use password")
                    .build()

                binding.btnBiometric.setOnClickListener {
                    biometricPrompt.authenticate(promptInfo)
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "Biometric hardware not available", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware currently unavailable", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric credentials enrolled", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun getUserInformation(accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkUtils.apiService.getUserInfo("Bearer $accessToken")
                withContext(Dispatchers.Main) {
                    val intentToHome = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intentToHome)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Failed to get user information", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}