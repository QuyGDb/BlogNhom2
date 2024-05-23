package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.Adapter.PostAdapter
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentProfileBinding
import com.example.blognhom2.model.PostInfo
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

        // Inflate the layout for this fragment
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the button and set a click listener
        view.findViewById<Button>(R.id.button2).setOnClickListener {
            // Replace current fragment with SecondFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, OwnerPost())
                .addToBackStack(null) // Optional: add to back stack
                .commit()
            println("Clicked")
        }
    }



}