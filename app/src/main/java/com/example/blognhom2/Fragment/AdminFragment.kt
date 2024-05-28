package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.blognhom2.R
import com.example.blognhom2.Utils.LogOutFunc
import com.example.blognhom2.databinding.FragmentAdminBinding


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
    }
}