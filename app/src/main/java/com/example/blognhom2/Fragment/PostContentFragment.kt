package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.blognhom2.API.BlogOwnerApi
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.databinding.FragmentPostContentBinding
import com.example.blognhom2.model.Category
import com.example.blognhom2.model.PostInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PostContentFragment : Fragment() {

    lateinit var post: PostInfo;
    lateinit var bookmarkPost: MutableList<PostInfo>
    private var _binding: FragmentPostContentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        SetDataForPostContent()
        bookmarkPost = getPostsInBookmarks() as MutableList<PostInfo>
        val view = binding.root
        return view
    }

    fun setData(post: PostInfo) {
        this.post = post
    }
    fun checkStatusToggleButon(){
        for (boomarkPost in bookmarkPost){
            if(boomarkPost.id == post.id)
                binding.BookMarkBtn.isChecked = true
            else
                binding.BookMarkBtn.isChecked = false
        }
    }
    fun BookmarkManager(){
        binding.BookMarkBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //call remove bookmark
                binding.BookMarkBtn.isChecked = false
            }
            else {
                // call add bookmark
                binding.BookMarkBtn.isChecked = true
            }
        }
    }
    fun SetDataForPostContent(){
        binding.postTitle.text = "      "+ post.title
        binding.postContent.text =  "      "+ post.content
        binding.postUser.text = post.user
        binding.postCategories.text = post.category
        binding.postTime.text = post.time
        Glide.with(requireContext())
            .load(post.img)
            .into(binding.postImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getPostsInBookmarks() : List<PostInfo> {
        bookmarkPost.clear()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        val call = api.getPosts(0);

        call.enqueue(object : Callback<List<PostInfo>> {
            override fun onResponse(call: Call<List<PostInfo>>, response: Response<List<PostInfo>>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val posts = response.body()
                posts?.let {
                    bookmarkPost.addAll(it);
                }

            }
            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                println(t.message)
            }
        })

        return bookmarkPost
    }
}