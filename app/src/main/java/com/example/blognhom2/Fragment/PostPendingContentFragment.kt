package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.blognhom2.API.AdminApi
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentPostPendingContentBinding
import com.example.blognhom2.model.PostInfo
import com.example.blognhom2.model.ResponseFormat
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager

class PostPendingContentFragment : Fragment() {

    lateinit var post : PostInfo
    private var _binding: FragmentPostPendingContentBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostPendingContentBinding.inflate(inflater, container, false)
        SetDataForPostContent()
        binding.cancelBtn.setOnClickListener {
            cancelPost()
            showDialog()
            //returnFragment()
        }
        binding.confirmBtn.setOnClickListener {
            confirmPost()
            returnFragment()

        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun setData(post: PostInfo) {
        this.post = post
    }
    fun returnFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, ConfirmPostFragment())
            .addToBackStack(null)
            .commit()
    }
    fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("CANCEL ?")
        builder.setMessage("Do you want cancel?")
        builder.setPositiveButton("OK") { dialog, which ->
            returnFragment()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Handle negative button click (e.g., dismiss dialog)
            dialog.dismiss()
        }

        builder.show()
    }
    fun SetDataForPostContent(){
        binding.postTitle.text = "      "+ post.title
        binding.postContent.text =  "      "+ post.content
        binding.postUser.text = post.user
        binding.postCategories.text = post.category
        binding.postTime.text = post.time
        Glide.with(requireContext())
            .load(post.img)
            .into(binding.postImage)
    }

    fun confirmPost() {
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

        val api = retrofit.create(AdminApi::class.java)
        val call = api.confirmPendingPost(post)
        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val status = response.body()

                println(status)


            }
            override fun onFailure(call: Call<ResponseFormat>, t: Throwable) {
                println(t.message)
            }
        })
    }

    fun cancelPost() {
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

        val api = retrofit.create(AdminApi::class.java)
        val call = api.cancelPendingPost(post)
        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val status = response.body()

                println(status)

            }
            override fun onFailure(call: Call<ResponseFormat>, t: Throwable) {
                println(t.message)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}