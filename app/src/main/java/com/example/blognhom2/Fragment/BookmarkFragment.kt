package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blognhom2.Adapter.PostAdapter
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentBookmarkBinding
import com.example.blognhom2.databinding.FragmentHomeBinding
import com.example.blognhom2.model.PostInfo


class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    lateinit var bookmarkPost: MutableList<PostInfo>
    lateinit var adapter : PostAdapter
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        val view = binding.root
        return view
    }


}