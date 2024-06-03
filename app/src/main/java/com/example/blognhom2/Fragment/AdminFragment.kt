package com.example.blognhom2.Fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.blognhom2.API.AdminApi
import com.example.blognhom2.R
import com.example.blognhom2.Utils.LogOutFunc
import com.example.blognhom2.databinding.FragmentAdminBinding
import com.example.blognhom2.model.MyCategory
import com.example.blognhom2.model.ResponseFormat
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.DriverManager


class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)

        val view = binding.root

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button2).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AllPostsFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.goToComfirmFragment_btn).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ConfirmPostFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener {
            LogOutFunc.logout(requireContext())
        }
        addCategory(view)
    }

    fun addCategory(view: View){
        view.findViewById<Button>(R.id.addCategory_btn).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val view = layoutInflater.inflate(R.layout.add_category, null)

            builder.setView(view)
            val dialog = builder.create()

             view.findViewById<Button>(R.id.addCategory).setOnClickListener {
                addCategoryHandler(view)
                dialog.dismiss()
             }
            view.findViewById<Button>(R.id.cancelCategory).setOnClickListener {
                dialog.dismiss()
            }
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
        }
    }
    fun addCategoryHandler(view: View) {
        val category : String = view.findViewById<EditText>(R.id.categoryEditText).text.toString()
        prepareAddCategory(category, 0)
    }
    fun prepareAddCategory(category: String, id : Int){
        val myCategory: MyCategory = MyCategory(id, category)
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
        val call = api.addCategory(myCategory)
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


