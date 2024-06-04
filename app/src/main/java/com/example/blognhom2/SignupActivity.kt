package com.example.blognhom2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blognhom2.API.AuthenticationAPI
import com.example.blognhom2.databinding.ActivitySignupBinding
import com.example.blognhom2.model.ResponseFormat
import com.example.blognhom2.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                // More robust password validation
                if (password.length < 8)
                {
                    Toast.makeText(this, "The minimum length of the password is 8 characters", Toast.LENGTH_SHORT).show()
                }
                else if (!isValidPassword(password)) {
                    Toast.makeText(this, "Password must contain uppercase letters, lowercase letters, special characters and numbers", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    prepare(email, password)
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")
        return passwordRegex.matches(password)
    }

    private fun prepare(username: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AuthenticationAPI::class.java)
        val user = User(username, password)
        val call = api.register(user)

        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {
                Log.d("SignupActivity", "Response: ${response.message()}")
                if (!response.isSuccessful) {
                    Log.e("SignupActivity", "Code: ${response.code()}")
                    Toast.makeText(this@SignupActivity, "${response.body()?.error}", Toast.LENGTH_SHORT).show()
                    return
                }

                val status = response.body()?.status
                if (status == true) {
                    Toast.makeText(this@SignupActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Navigate to LoginActivity
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseFormat>, t: Throwable) {
                Log.e("SignupActivity", "Registration failed: ${t.message}")
                Toast.makeText(this@SignupActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}