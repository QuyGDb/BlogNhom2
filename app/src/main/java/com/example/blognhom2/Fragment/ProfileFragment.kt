package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.blognhom2.R
import com.example.blognhom2.Utils.LogOutFunc
import com.example.blognhom2.databinding.FragmentProfileBinding


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
        //Phúc viết api lấy username xong gán xuống dưới
        binding.username.text = ""
    }



}