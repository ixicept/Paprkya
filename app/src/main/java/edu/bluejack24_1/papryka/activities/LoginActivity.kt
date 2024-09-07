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
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.utils.NetworkUtils
import edu.bluejack24_1.papryka.utils.SnackBarUtils
import edu.bluejack24_1.papryka.utils.TokenManager
import edu.bluejack24_1.papryka.utils.getCurrentLanguage
import edu.bluejack24_1.papryka.utils.setLanguageForApp
import edu.bluejack24_1.papryka.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentLanguage = getCurrentLanguage(this)
        if (currentLanguage != null) {
            setLanguageForApp(this, currentLanguage)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        binding.btnLogin.setOnClickListener {
            val initial = binding.etInitial.text.toString()
            val password = binding.etPassword.text.toString()

            if (initial.isNotEmpty() && password.isNotEmpty()) {
                if (initial.indexOf('-', 0) != 4) {
                    Toast.makeText(this, "Initial is not valid", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.loginUser(initial, password)
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.setupBiometricPrompt(this)

        binding.btnBiometric.setOnClickListener {
            val accessToken = TokenManager.getAccessToken(this)
            if (accessToken != null) userViewModel.authenticateBiometric()
            else Toast.makeText(this, "Please login with password first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        userViewModel.accessToken.observe(this) { token ->
            token?.let {
                TokenManager.saveAccessToken(this, it)

                userViewModel.toHome(this)
            }
        }

        userViewModel.loginError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.biometricError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}