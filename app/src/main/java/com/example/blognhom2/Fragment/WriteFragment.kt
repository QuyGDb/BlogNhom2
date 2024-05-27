package com.example.blognhom2.Fragment

//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.bumptech.glide.util.Util
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.databinding.FragmentWriteBinding
import com.example.blognhom2.model.Category
import com.example.blognhom2.model.FileFormat
import com.example.blognhom2.model.MyPost
import com.example.blognhom2.model.ResponseFormat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.sql.DriverManager


class WriteFragment : Fragment() {


    var imgUrl: String? = ""
    //    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/android-97dcb.appspot.com/o/Images%2F-Nyj1Hv14lQZoY5s9ABS?alt=media&token=8443ca3a-4c60-4adc-8a3e-a5ee46a1d98f"
    val imageUrl = ""
    private var categoriesList = mutableListOf<Category>()
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private var uri: Uri? = null
    private val REQUEST_CODE = 100;


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

        //Set dafaut image View
//        Glide.with(this)
//            .load(imageUrl)
//            .into(binding.wImage)

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
    private fun saveData(title: String, content: String, category: String) {
        val postInfo: MyPost = MyPost(null, imgUrl, title, category, content);

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