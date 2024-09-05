package edu.bluejack24_1.papryka.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import edu.bluejack24_1.papryka.databinding.ActivityLoginBinding
import edu.bluejack24_1.papryka.utils.getCurrentLanguage
import edu.bluejack24_1.papryka.utils.setLanguageForApp
import edu.bluejack24_1.papryka.viewmodels.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentLanguage = getCurrentLanguage(this)
        if (currentLanguage != null) {
            setLanguageForApp(this, currentLanguage)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.accessToken.observe(this) { token ->
            if (token != null) {
                val sharedPreferences = getSharedPreferences("AppPreference", MODE_PRIVATE)
                sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply()

                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                val intentToHome = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intentToHome)
                finish()
            }
        }

        userViewModel.loginError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val initial = binding.etInitial.text.toString()
            val password = binding.etPassword.text.toString()

            if (initial.isNotEmpty() && password.isNotEmpty()) {

                if (initial.indexOf('-', 0) != 4) {
                    Toast.makeText(this@LoginActivity, "Initial is not valid", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                userViewModel.loginUser(initial, password)

            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                biometricPrompt = BiometricPrompt(
                    this,
                    ContextCompat.getMainExecutor(this),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication error: $errString",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            val sharedPreferences =
                                getSharedPreferences("AppPreference", MODE_PRIVATE)
                            val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
                            if (accessToken != null) {
                                userViewModel.fetchUserInformation(accessToken)
                                val intentToHome =
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intentToHome)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Access token not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
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
                Toast.makeText(this, "Biometric hardware currently unavailable", Toast.LENGTH_SHORT)
                    .show()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric credentials enrolled", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                startActivity(intent)
            }
        }
    }

}