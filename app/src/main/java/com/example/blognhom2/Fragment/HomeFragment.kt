package com.example.blognhom2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blognhom2.Adapter.PostAdapter
import com.example.blognhom2.databinding.FragmentHomeBinding
import com.example.blognhom2.model.Post
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

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
        preparePostData()
        SetPostAdapter()
        SetSearchView()
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
    companion object {
        private var postList = mutableListOf<Post>()

        fun preparePostData() : List<Post> {
            postList.clear()
            val post1 = Post("123",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg",
                "The Dark Knight",
                "Movie",
                "Để giới hạn số ký tự được hiển thị trong một TextView trong ứng dụng Android Studio, bạn có thể sử dụng một số phương pháp khác nhau. Dưới đây là một số cách phổ biến:"
            )
            val post2 = Post("456",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg",
                "Sekiro: Shadows Die Twice - Vẻ đẹp ẩn sau lớp vỏ khó nhằn",
                "Anime",
                "testcontent"
            )
            val post3 = Post("789",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png",
                "Elden Ring - Siêu phẩm hay game rác?",
                "Music",
                "testcontent"
            )
            val post4 = Post("0123",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg",
                "Vô gian đạo",
                "Movie",
                "testcontent"
            )
            val post5 = Post("1234",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg",
                "Lie of P",
                "Game",
                "testcontent"
            )
            val post6 = Post("5678",
                "Trinhquy",
                LocalDate.now(),
                "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png",
                "Ngọt",
                "Music",
                "testcontent"
            )
            postList.add(post1)
            postList.add(post2)
            postList.add(post3)
            postList.add(post4)
            postList.add(post5)
            postList.add(post6)
            return postList
        }
    }
    private fun SetPostAdapter(){



        adapter = PostAdapter(postList)
        binding.PostRecyclerView.adapter = adapter
        binding.PostRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)

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
    private fun searchList(query : String?){
    if (query != null){
        val filteredList = mutableListOf<Post>()
        for (post in postList){
            if(post.title.lowercase(Locale.ROOT).contains(query)){
                filteredList.add(post)
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(requireContext(), "Không tìm thấy bài viết", Toast.LENGTH_LONG).show()
        } else{
            adapter.setFilteredList(filteredList)
        }
    }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
