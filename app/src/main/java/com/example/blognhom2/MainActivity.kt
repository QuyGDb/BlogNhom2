package com.example.blognhom2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.blognhom2.Fragment.*
import com.example.blognhom2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //chuyển qua các fragment
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.categories -> replaceFragment(CategoriesFragment())
                R.id.write -> replaceFragment(WriteFragment())
                R.id.bookmark -> replaceFragment(BookmarkFragment())
                R.id.profile -> replaceFragment(ProfileFragment())

                else ->{

                }

            }
            true
        }
    }
    // hàm chuyển fragment
    private  fun  replaceFragment(fragment : Fragment){
    val fragmentManager = supportFragmentManager
        val  fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}