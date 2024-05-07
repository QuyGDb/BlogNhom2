package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentCategoriesBinding
import com.example.blognhom2.model.Categories
import com.example.blognhom2.model.Post


class CategoriesFragment : Fragment() {
    private var categoriesList = mutableListOf<Categories>()
    private var posts = mutableListOf<Post>()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
        val view = binding.root
        return view
    }
    private fun prepareData(){
        posts = HomeFragment.preparePostData() as MutableList<Post>

    }
    private fun SetCategoriesAdapter(){

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}