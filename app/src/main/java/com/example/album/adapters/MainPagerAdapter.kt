package com.example.album.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.album.ui.collections.CollectionsFragment
import com.example.album.ui.home.HomeFragment
import com.example.album.ui.recent.RecentFragment
import com.example.album.ui.trending.TrendingFragment

/*
created by  Shivang Yadav on 27-04-2024
gitHub: https://github.com/Iamshivang
project: AlbuM
description: Create a class extending from FragmentStateAdapter to swipe Fragments.
*/

private const val NUM_TABS = 4

class MainPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle)  {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return HomeFragment()
            1 -> return TrendingFragment()
            2 -> return RecentFragment()
        }
        return CollectionsFragment()
    }
}