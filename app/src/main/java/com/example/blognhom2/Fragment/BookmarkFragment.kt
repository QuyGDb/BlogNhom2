package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.Adapter.PostAdapter
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentBookmarkBinding
import com.example.blognhom2.model.PostInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager


class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    lateinit var bookmarkPosts: List<PostInfo>
    lateinit var adapter : PostAdapter
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        bookmarkPosts = getPostsInBookmarks()
        SetPostAdapter()
        val view = binding.root
        return view
    }
    companion object {

        var bookmarkPosts = mutableListOf<PostInfo>()
        private fun getPostsInBookmarks(): List<PostInfo> {
            bookmarkPosts.clear()
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
            val call = api.getPostsInBookmarks(0);

            call.enqueue(object : Callback<List<PostInfo>> {
                override fun onResponse(
                    call: Call<List<PostInfo>>,
                    response: Response<List<PostInfo>>
                ) {
                    println("ResponsePost")
                    println(response)
                    if (!response.isSuccessful) {
                        println("Code: " + response.code())
                        return
                    }

                    val posts = response.body()
                    posts?.let {
                        bookmarkPosts.addAll(it);
                    }
                    println(posts)

                }

                override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                    println(t.message)
                }
            })

            return bookmarkPosts
        }
    }
    private fun SetPostAdapter() {
        adapter = PostAdapter(bookmarkPosts)
        binding.bookmarkRecycleView.adapter = adapter
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.bookmarkRecycleView.layoutManager = layoutManager
    }
}