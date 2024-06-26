package com.example.blognhom2.Fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.blognhom2.API.AdminApi
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentEditPostBinding
import com.example.blognhom2.model.*
import com.google.android.material.snackbar.Snackbar
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

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null

    var imgUrl: String? = ""
    private var categoryId: Int? = null
    private var categoriesList = mutableListOf<Category>()
    private val binding get() = _binding!!

    lateinit var post: PostInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        prepareData()
        setDataForEditFragment()

        val pickImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {
                binding.edImage.setImageURI(uri)
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
                            return;
                        }
                        imgUrl = response.body()?.imageUrl;
                        println("ImageURL: $imgUrl")
                    }

                    override fun onFailure(call: Call<FileFormat>, t: Throwable) {
                        println("Error: ${t.message}")
                    }
                })
            }
        }

        binding.edPickImgBtn.setOnClickListener{
            println("upload iMage")
            pickImage.launch("image/*")
            pickImage.launch("image/*")
        }

        binding.edDeleteBtn.setOnClickListener {
            showDialog()
        }

        binding.edCategoies.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Toast.makeText(requireContext(), position, Toast.LENGTH_LONG).show()
                categoryId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        });
//
        binding.edSubmitBtn.setOnClickListener {
            val title = binding.edTitle.text.toString()
            val content = binding.edContent.text.toString()
            val category = categoriesList[categoryId!!].category
            if (title.isNotEmpty() && content.isNotEmpty()) updateData(post.id, title, content, category)
            else Toast.makeText(requireContext(), "Please Fill ALl Data", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    fun returnFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, OwnerPostFragment())
            .addToBackStack(null)
            .commit()
    }
    fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("DELETE ?")
        builder.setMessage("Do you want to Delete?")
        builder.setPositiveButton("OK") { dialog, which ->
            deletePost()
            returnFragment()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Handle negative button click (e.g., dismiss dialog)
            dialog.dismiss()
        }

        builder.show()
    }

    fun setData(post: PostInfo) {
        this.post = post
        imgUrl = post.img
    }
    private fun completeUpdatePost() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Update Success !!!",
            Snackbar.LENGTH_LONG
        ).show()
    }

    fun deletePost() {
        println("abc")
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
        val call = api.deletePost(post.id)
        call.enqueue(object : Callback<ResponseFormat> {
            override fun onResponse(call: Call<ResponseFormat>, response: Response<ResponseFormat>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    //Log.e("decode", response.errorBody().toString())
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
        var call = api.getCategories();
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

    private fun updateData(postId: Int, title: String, content: String, category: String) {
        updateUserPost(postId, title, content, category)
    }

    private fun updateUserPost(id: Int, title: String, content: String, category: String) {
        val postInfo: MyPost = MyPost(id, imgUrl, title, category, content);
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
        completeUpdatePost()
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

    fun setDataForEditFragment() {
        binding.edTitle.setText(post.title)
        Glide.with(this)
            .load(post.img)
            .into(binding.edImage)

//        val adapter = binding.edCategoies.adapter as ArrayAdapter<String>
//        val stringList = mutableListOf<String>()
//        for (i in 0 until adapter.count) {
//            stringList.add(adapter.getItem(i)!!)
//        }
//        val defaultPosition = stringList.indexOf(post.category)
//        categoryId = defaultPosition
//        binding.edCategoies.setSelection(defaultPosition)
        binding.edContent.setText(post.content)
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
        binding.edCategoies.adapter = adapter
    }
}