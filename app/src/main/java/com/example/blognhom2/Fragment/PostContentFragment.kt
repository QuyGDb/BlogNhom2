package com.example.blognhom2.Fragment

import android.os.Bundle
import android.os.Debug
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.databinding.FragmentPostContentBinding
import com.example.blognhom2.model.Category
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


class PostContentFragment : Fragment() {


    lateinit var post : PostInfo;

    private var isBookmarkPost = MutableLiveData<Boolean>();

    private var _binding: FragmentPostContentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        SetDataForPostContent()

        checkPostInBookmark()
        BookmarkManager();

        val view = binding.root
        return view
    }
    fun BookmarkManager(){
        binding.BookMarkBtn.setOnClickListener {
            if (isBookmarkPost.value == true) {
                println("In bookmark")
                removePostFromBookmark()
                isBookmarkPost.value = false
            } else {
                println("Not in bookmark")
                addPostToBookmark()
                isBookmarkPost.value = true
            }
        }

    }
//    kt post co trong bookmark hay khong

    private fun checkPostInBookmark() {
        var isInBookmark = false;
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

        val api = retrofit.create(BlogOwnerApi::class.java)
        val call = api.isInBookmark(post.id)

        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        isBookmarkPost.value = body.status

                        binding.BookMarkBtn.isChecked = isBookmarkPost.value!!
                    }
                }
            }
            override fun onFailure(call: Call<ResponseFormat>, t: Throwable) {
                println(t.message)
                println("loi")
            }
        })
    }
    //    add post to bookmark
    private fun addPostToBookmark() {
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

        val api = retrofit.create(BlogOwnerApi::class.java)
        val call = api.addPostToBookmarks(post)
        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val status = response.body()



            }
            override fun onFailure(call: Call<ResponseFormat>, t: Throwable) {
                println(t.message)
            }
        })
    }

    //    remove post from bookmark
    private fun removePostFromBookmark() {
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

        val api = retrofit.create(BlogOwnerApi::class.java)
        val call = api.removePostFromBookmarks(post.id)
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


    fun setData(post: PostInfo) {
        this.post = post
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}