package com.example.threadss.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.threadss.R
import com.example.threadss.adapter.ViewPagerAdapter
import com.example.threadss.databinding.ActivityFollowerBinding
import com.google.android.material.tabs.TabLayoutMediator

class FollowerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFollowerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle )
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabs, binding.viewPager2){tab, position->
            when(position){
                0->{
                    tab.text="Followers"
                }
                1->{
                    tab.text="Following"
                }
            }
        }.attach()



    }
}