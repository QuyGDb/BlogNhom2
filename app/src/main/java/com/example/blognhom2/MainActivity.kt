package com.example.blognhom2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import androidx.fragment.app.Fragment
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.Fragment.*
import com.example.blognhom2.databinding.ActivityMainBinding
import com.example.blognhom2.model.UserAuthentication
import com.google.android.material.navigation.NavigationBarView
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        transFragment()
        //chuyển qua các fragment

    }

    // hàm chuyển fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun transFragment() {

        binding.bottomBar.setOnItemSelectedListener {

            when (it) {

                0 -> replaceFragment(HomeFragment())
                1 -> replaceFragment(CategoriesFragment())
                2 -> replaceFragment(WriteFragment())
                3 -> replaceFragment(BookmarkFragment())
                4 -> if(isUserLogin()) {
                    replaceFragment(ProfileFragment())
                } else {
                    replaceFragment(AdminFragment())
                }

                else -> {

                }

            }
        }
    }

    private fun isUserLogin(): Boolean {
        var role: String? = null
            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val orginal : Request = chain.request()
                    val requestBuilder = orginal.newBuilder()

                    val cookies = CookieManager.getInstance().getCookie("http://10.0.2.2:8081/")

                    DriverManager.println("Cookies $cookies")
                    if(cookies != null) {
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
            val api = retrofit.create(BlogOwnerApi::class.java)

            val call = api.getUser()

            role = ""
            call.enqueue(object : Callback<UserAuthentication> {
                override fun onResponse(
                    call: Call<UserAuthentication>,
                    response: Response<UserAuthentication>
                ) {
                    println(response)
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            role = it.roles.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<UserAuthentication>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        //return false // Placeholder: replace with actual logic
        if(role == "USER") {
            return true
        } else {
            return false
        }
    }
}