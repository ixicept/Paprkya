package edu.bluejack24_1.papryka.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.ActivityLoginBinding

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
                // TODO: Messier account
                if (initial == "tes@gmail.com" && password == "pass") {
                    val intentToHome = Intent(this, MainActivity::class.java)
                    startActivity(intentToHome)
                    finish()
                } else {
                    Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show()
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