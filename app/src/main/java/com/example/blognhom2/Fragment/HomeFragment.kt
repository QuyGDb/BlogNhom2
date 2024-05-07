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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private var _binding: FragmentHomeBinding? = null
    private var postList = mutableListOf<Post>()
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
    private fun SetPostAdapter(){


        val post1 = Post("Trinhquy",LocalDate.now(), "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg", "The Dark Knight", "Movie","Để giới hạn số ký tự được hiển thị trong một TextView trong ứng dụng Android Studio, bạn có thể sử dụng một số phương pháp khác nhau. Dưới đây là một số cách phổ biến:")
        val post2 = Post("Trinhquy",LocalDate.now(), "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg", "Sekiro: Shadows Die Twice - Vẻ đẹp ẩn sau lớp vỏ khó nhằn", "Game", "testcontent")
        val post3 = Post("Trinhquy", LocalDate.now(),"https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png", "Elden Ring - Siêu phẩm hay game rác?", "Game","testcontent")
        val post4 = Post("Trinhquy",LocalDate.now(), "https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/56a4d870-e3f1-4085-b6ef-2aae4d17641b.jpg", "The Dark Knight", "Movie","testcontent")
        val post5 = Post("Trinhquy", LocalDate.now(),"https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/b8f2ad85-2cae-47d5-af6a-1db3432fe5c3.jpg", "Sekiro: Shadows Die Twice - Vẻ đẹp ẩn sau lớp vỏ khó nhằn", "Game","testcontent")
        val post6 = Post("Trinhquy", LocalDate.now(),"https://raw.githubusercontent.com/jackson22153fake/BlogImgRepository/main/849dca8f-4ab9-4eb9-99db-34ad6c74d0c2.png", "Elden Ring - Siêu phẩm hay game rác?", "Game","testcontent")
        postList.add(post1)
        postList.add(post2)
        postList.add(post3)
        postList.add(post4)
        postList.add(post5)
        postList.add(post6)
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
                filterList(newText)
                return true
            }

        } )
    }
    private fun filterList(query : String?){
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
