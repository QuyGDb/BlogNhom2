package com.example.blognhom2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blognhom2.Fragment.AdminFragment
import com.example.blognhom2.Fragment.BookmarkFragment
import com.example.blognhom2.Fragment.CategoriesFragment
import com.example.blognhom2.Fragment.HomeFragment
import com.example.blognhom2.Fragment.ProfileFragment
import com.example.blognhom2.Fragment.WriteFragment
import com.example.blognhom2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        transFragment()
        //chuyển qua các fragment

    }

    // hàm chuyển fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun transFragment() {
        binding.bottomBar.setOnItemSelectedListener {

            when (it) {

                0 -> replaceFragment(HomeFragment())
                1 -> replaceFragment(CategoriesFragment())
                2 -> replaceFragment(WriteFragment())
                3 -> replaceFragment(BookmarkFragment())
                4 -> if(userRole == "USER") {
                    replaceFragment(ProfileFragment())
                } else {
                    replaceFragment(AdminFragment())
                }
                else -> {

                }

            }
        }
    }
}