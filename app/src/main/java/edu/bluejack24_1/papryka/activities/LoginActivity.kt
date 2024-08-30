package edu.bluejack24_1.papryka.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack24_1.papryka.databinding.ActivityLoginBinding
import edu.bluejack24_1.papryka.models.LoginRequest
import edu.bluejack24_1.papryka.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

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
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
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

        binding.btnBiometric.setOnClickListener {
            // TODO: Biometric
        }
    }
}