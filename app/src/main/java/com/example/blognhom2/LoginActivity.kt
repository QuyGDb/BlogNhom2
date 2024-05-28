package com.example.blognhom2

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.blognhom2.API.AuthenticationAPI
import com.example.blognhom2.model.UserAuthentication
import com.example.blognhom2.databinding.ActivityLoginBinding
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager.println

var userRole: String?= null
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                prepare(email, password)
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
//            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(mainIntent)
//            finish() // Close the LoginActivity
        }

        binding.forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
//            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            // Uncomment and implement the compareEmail function if needed
            // view.findViewById<Button>(R.id.btnReset).setOnClickListener {
            //    compareEmail(userEmail)
            //    dialog.dismiss()
            // }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }

        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

    private fun prepare(username: String, password: String) {
        val authToken = Credentials.basic(username, password)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", authToken)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val api = retrofit.create(AuthenticationAPI::class.java)
        val call = api.login()

        call.enqueue(object : Callback<UserAuthentication> {
            override fun onResponse(call: Call<UserAuthentication>, response: Response<UserAuthentication>) {
                Log.d("LoginActivity", "Response: ${response.headers()}")
                if (!response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Username or password is wrong!! Try again", Toast.LENGTH_SHORT).show()
                    return
                }
                val user = response.body()
                if (user != null) {

                    userRole = user.roles[0]

                    Log.d("LoginActivity", "User authenticated: $user")
                    // Handle the authenticated user here
                    val setCookieHeader = response.headers()["Set-Cookie"]
                    if (setCookieHeader != null) {
                        val cookies = setCookieHeader.split(";")
                        for (cookie in cookies) {
                            if (cookie.startsWith("JSESSIONID")) {
                                val jsessionId = cookie.split("=")[1]
                                println("JSESSIONID: $jsessionId")
                                // Store the JSESSIONID cookie in SharedPreferences or CookieManager
                                val cookieManager = CookieManager.getInstance()
                                cookieManager.setCookie("http://10.0.2.2:8081/", "JSESSIONID=$jsessionId; HttpOnly")
                            }
                        }
                    }
                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish() // Close the LoginActivity
                } else {
                    Log.e("LoginActivity", "Failed to get user details")
                }
            }

            override fun onFailure(call: Call<UserAuthentication>, t: Throwable) {
                Log.e("LoginActivity", t.message ?: "Unknown error")
                Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}