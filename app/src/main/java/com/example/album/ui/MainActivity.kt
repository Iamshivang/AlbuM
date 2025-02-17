package com.example.album.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.album.adapters.MainPagerAdapter
import com.example.album.databinding.ActivityMainBinding
import com.example.album.ui.home.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG= "MainActivity"
    val viewModel: MainViewModel  by viewModels()
    private lateinit var adapter: MainPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews() {

        viewPager= binding.pager
        tabLayout= binding.tabLayout
        adapter= MainPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter= adapter

        val list= listOf("HOME", "TRENDING", "RECENT", "COLLECTIONS")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = list[position]
        }.attach()
    }
}