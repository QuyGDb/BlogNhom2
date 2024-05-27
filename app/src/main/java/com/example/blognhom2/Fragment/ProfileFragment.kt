package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.R
import com.example.blognhom2.Utils.LogOutFunc
import com.example.blognhom2.databinding.FragmentProfileBinding
import com.example.blognhom2.model.UserAuthentication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUserName()

        val view = binding.root

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.allPosts_btn).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, OwnerPostFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener {
            LogOutFunc.logout(requireContext())
        }
    }

    fun setUserName(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(BlogOwnerApi::class.java)

        val call = api.getUser()

        call.enqueue(object : Callback<UserAuthentication> {
            override fun onResponse(
                call: Call<UserAuthentication>,
                response: Response<UserAuthentication>
            ) {
                println(response)
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        // Retrieve the username
                        val username = it.username
                        binding.username.text = username
                    }
                }
            }
        })
    }
}