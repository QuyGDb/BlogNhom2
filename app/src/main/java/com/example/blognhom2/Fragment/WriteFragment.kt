package com.example.blognhom2.Fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.databinding.FragmentWriteBinding
import com.example.blognhom2.model.Category
import com.example.blognhom2.model.FileFormat
import com.example.blognhom2.model.MyPost
import com.example.blognhom2.model.ResponseFormat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.sql.DriverManager


class WriteFragment : Fragment() {

    var imgUrl: String? = ""
    private var categoriesList = mutableListOf<Category>()
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        prepareData()

        //Pick image action
        val pickImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {
                binding.wImage.setImageURI(uri)
                val file = createTempFile(uri)
                val requestBody = file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val httpClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original: Request = chain.request()
                        val requestBuilder = original.newBuilder()
                        requestBuilder.addHeader("Content-Type", "multipart/form-data")

                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
                val api = retrofit.create(PostApi::class.java)

                val call = api.uploadImage(multipartBody)
                println("filename: ${file.name}")
                call.enqueue(object : Callback<FileFormat> {
                    override fun onResponse(call: Call<FileFormat>, response: Response<FileFormat>) {
                        println(response)
                        if(!response.isSuccessful){
                            println("Code ${response.code()}")
                            return
                        }
                        imgUrl = response.body()?.imageUrl
                        println("ImageURL: $imgUrl")
                    }

                    override fun onFailure(call: Call<FileFormat>, t: Throwable) {
                        println("Error: ${t.message}")
                    }
                })
            }
        }

        binding.wPickImgBtn.setOnClickListener{
            println("upload iMage")
            pickImage.launch("image/*")
        }

        //Upload post Logic
        binding.wSubmitBtn.setOnClickListener {
            val title = binding.wTitle.text.toString()
            val content = binding.wContent.text.toString()
            val category = categoriesList[0].category
            if (title.isNotEmpty() && content.isNotEmpty()) saveData(title, content, category)
            else Toast.makeText(requireContext(), "Please Fill ALl Data", Toast.LENGTH_LONG).show()
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun createTempFile(uri: Uri): File {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        if (inputStream != null) {
            val file = File.createTempFile("image", ".jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            return file
        } else {
            throw FileNotFoundException()
        }
    }
    //    update post
    private fun updateUserPost(id: Int, title: String, content: String, category: String) {
        val postInfo: MyPost = MyPost(id, imgUrl, title, category, content)
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
        val call = api.updatePost(postInfo)
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


    //    add new post
    private fun saveData(title: String, content: String, category: String) {
        val postInfo: MyPost = MyPost(null, imgUrl, title, category, content)

        println(postInfo)
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
        val call = api.createPost(postInfo)
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

    private fun prepareData(){
        categoriesList.clear()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        var call = api.getCategories()
        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val categories = response.body()
                println(categories)
                categories?.let {
                    categoriesList.addAll(it)
                }
                SetCategoriesAdapter()

            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun SetCategoriesAdapter(){
        val categoryStrings = if (categoriesList.isEmpty()) {
            // Handle empty list case (e.g., empty array or default message)
            emptyArray<String>()
        } else {
            categoriesList.map { it.category }.toTypedArray()
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryStrings)
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryStrings)
        binding.wCategoies.adapter = adapter
    }

}