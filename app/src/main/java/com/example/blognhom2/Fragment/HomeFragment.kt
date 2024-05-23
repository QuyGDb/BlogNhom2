package com.example.blognhom2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.Adapter.PostAdapter
import com.example.blognhom2.databinding.FragmentHomeBinding
import com.example.blognhom2.model.PostInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class HomeFragment : Fragment() {
    private var isLoading = false
    private var visibleThreshold = 5 // Number of items from the bottom of the list at which loading more is triggered
    private var offset = 0 // The offset for loading more posts

    private var _binding: FragmentHomeBinding? = null

    lateinit var adapter : PostAdapter
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initImageView()
        SetSearchView()
        preparePostData()
        SetPostAdapter();
        // Inflate the layout for this fragment
        val view = binding.root
        return view
    }
    fun initImageView() {
        val imageList = ArrayList<SlideModel>() // Create image list


        imageList.add(SlideModel("https://kenh14cdn.com/thumb_w/660/203336854389633024/2022/5/25/photo-1-1653461340935426739125.jpg", "Chào bạn dã đến với blog của nhóm 2 chúng tôi."))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
    }

    private var postList = mutableListOf<PostInfo>()

    private fun preparePostData() : List<PostInfo> {
        postList.clear()

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
                    postList.addAll(it);
                }
                updateAdapter()
            }

            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                println(t.message)
            }
        })

        val post1 = PostInfo(123,
            "TQ",
            "29/01/2003",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg",
            "The Dark Knight",
            "Movie",
            "Để giới hạn số ký tự được hiển thị trong một TextView trong ứng dụng Android Studio, bạn có thể sử dụng một số phương pháp khác nhau. Dưới đây là một số cách phổ biến:",
            "ok"
        )
        val post2 = PostInfo(456,
            "TQ",
            "",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg",
            "Sekiro: Shadows Die Twice - Vẻ đẹp ẩn sau lớp vỏ khó nhằn",
            "Anime",
            "testcontent",
            "ok"
        )
        val post3 = PostInfo(789,
            "TQ",
            "LocalDate.now()",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png",
            "Elden Ring - Siêu phẩm hay game rác?",
            "Music",
            "testcontent",
            "Ok"
        )
        val post4 = PostInfo(0,
            "12",
            "LocalDate.now()",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg",
            "Vô gian đạo",
            "Movie",
            "testcontent",
            "Ok"
        )
        val post5 = PostInfo(1234,
            "TQ",
            "LocalDate.now()",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg",
            "Lie of P",
            "Game",
            "testcontent",
            "ok"
        )
        val post6 = PostInfo(5678,
            "1",
            "LocalDate.now()",
            "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png",
            "Ngọt",
            "Music",
            "testcontent",
            "ok"
        )
        postList.add(post1)
        postList.add(post2)
        postList.add(post3)
        postList.add(post4)
        postList.add(post5)
        postList.add(post6)

        return postList
    }

    private fun updateAdapter() {
        adapter.setFilteredList(postList)
    }
    private fun SetPostAdapter(){
        adapter = PostAdapter(postList)
        binding.PostRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.PostRecyclerView.layoutManager = layoutManager

        // Add the onScrollListener to your RecyclerView
        binding.PostRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached, load more items
                    loadMoreItems()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMoreItems() {
        // Increase your offset
        offset += 1

        // Call your API here
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        val call = api.getPosts(offset)

        call.enqueue(object : Callback<List<PostInfo>> {
            override fun onResponse(call: Call<List<PostInfo>>, response: Response<List<PostInfo>>) {
                println(response)
                if (response.isSuccessful) {
                    val posts = response.body()
                    posts?.let {
                        postList.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                // Handle the error
                isLoading = false
            }
        })
    }

    private fun SetSearchView(){
        binding.seachView.clearFocus()
        binding.seachView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList(newText)
                return true
            }

        } )
    }
    private fun searchList(query : String?, ){
        if (query != null){
            searchPost(query) { postInfos ->
                if (postInfos != null) {
                    val filteredList = linkedSetOf<PostInfo>()
                    filteredList.addAll(postInfos)
                    if(filteredList.isEmpty()){
                        Toast.makeText(requireContext(), "Không tìm thấy bài viết", Toast.LENGTH_LONG).show()
                    } else{
                        adapter.setFilteredList(filteredList.toMutableList())
                    }
                } else {
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun searchPost(query: String, onResult: (List<PostInfo>?) -> Unit){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        val call = api.searchPosts(0, query);
        println("searchPost")

        call.enqueue(object : Callback<List<PostInfo>> {
            override fun onResponse(call: Call<List<PostInfo>>, response: Response<List<PostInfo>>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    onResult(null);
                    return
                }
                val posts = response.body()
                println(posts)
                onResult(posts);
            }

            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                println(t.message)
                onResult(null);
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}