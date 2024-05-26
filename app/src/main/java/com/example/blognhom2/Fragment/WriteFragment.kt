package com.example.blognhom2.Fragment

import android.R
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.databinding.FragmentWriteBinding
import com.example.blognhom2.model.Category
import com.example.blognhom2.model.MyPost
import com.example.blognhom2.model.PostInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bumptech.glide.Glide
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var imgUrl: String? = ""

    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/android-97dcb.appspot.com/o/Images%2F-Nyj1Hv14lQZoY5s9ABS?alt=media&token=8443ca3a-4c60-4adc-8a3e-a5ee46a1d98f"

    private var categoriesList = mutableListOf<Category>()

    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private var uri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("contacts")
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        prepareData()

        //Pick image action
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.wImage.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        //Set dafaut image View
        Glide.with(this)
            .load(imageUrl)
            .into(binding.wImage)

        binding.wPickImgBtn.setOnClickListener{
            pickImage.launch("image/*")
        }

        //Upload post Logic
        binding.wSubmitBtn.setOnClickListener {
            val title = binding.wTitle.text.toString()
            val content = binding.wContent.text.toString()
            val category = "categoriesList[0].category"
            if (title.isNotEmpty() && content.isNotEmpty()) saveData(title, content, category)
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun saveData(title: String, content: String, category: String) {
        val id = firebaseRef.push().key!!
        var post : MyPost
        uri?.let {
            storageRef.child(id).putFile(it)
                .addOnSuccessListener {task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {url ->
                            Toast.makeText(requireContext(), "Image success", Toast.LENGTH_LONG).show()
                            imgUrl = url.toString()
                            val today = LocalDate.now()

                            val year = today.year
                            val month = today.monthValue // Returns month as an enum (e.g., MAY)
                            val dayOfMonth = today.dayOfMonth
                            val formattedDate = "$year-${month}-${dayOfMonth}"

                            //Post cần up lên
                            post = MyPost(title, imageUrl, content, category, formattedDate)
                        }
                }
        }
    }

    private fun prepareData(){
        categoriesList.clear()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        var call = api.getCategories();
        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

                val categories = response.body()
                println(categories)
                categories?.let {
                    categoriesList.addAll(it)
                }
                SetCategoriesAdapter()

            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun SetCategoriesAdapter(){
        val categoryStrings = if (categoriesList.isEmpty()) {
            // Handle empty list case (e.g., empty array or default message)
            emptyArray<String>()
        } else {
            categoriesList.map { it.category }.toTypedArray()
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryStrings)
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryStrings)
        binding.wCategoies.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}