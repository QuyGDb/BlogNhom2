package com.example.blognhom2.Utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import com.example.blognhom2.API.AuthenticationAPI
import com.example.blognhom2.LoginActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager

object LogOutFunc{
     fun logout(context : Context) {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder = original.newBuilder()

                // Get the cookies for this URL
                val cookies = CookieManager.getInstance().getCookie("http://10.0.2.2:8081/")

                DriverManager.println("Cookies $cookies")
                if (cookies != null) {
                    // Add the cookies to the request header
                    requestBuilder.addHeader("Cookie", cookies)
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val api = retrofit.create(AuthenticationAPI::class.java)
        val call = api.logout()

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("LogoutActivity", "Response: ${response.raw()}")
                if (!response.isSuccessful) {
                    Log.e("LogoutActivity", "Code: ${response.code()}")
                    return
                }
                // Logout successful, navigate to login screen
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                if (context is AppCompatActivity) {
                    context.finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("LogoutActivity", "Logout failed: ${t.message}")
            }
        })
    }
}