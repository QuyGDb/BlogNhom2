package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.blognhom2.R
import com.example.blognhom2.databinding.FragmentHomeBinding
import com.example.blognhom2.databinding.FragmentPostContentBinding
import com.example.blognhom2.model.Post

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostContentFragment : Fragment() {

    lateinit var post : Post;
    private var _binding: FragmentPostContentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        SetDataForPostContent()
        val view = binding.root
        return view
    }
    fun setData(post: Post) {
        this.post = post
    }
    fun SetDataForPostContent(){
        binding.postTitle.text = "      "+ post.title
        binding.postContent.text =  "      "+ post.postContent
        binding.postUser.text = post.user
        binding.postCategories.text = post.categories
        binding.postTime.text = post.time.toString()
        Glide.with(requireContext())
            .load(post.postImg)
            .into(binding.postImage)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}