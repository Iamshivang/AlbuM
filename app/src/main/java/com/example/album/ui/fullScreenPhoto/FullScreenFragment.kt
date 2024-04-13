package com.example.album.ui.fullScreenPhoto

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.album.R
import com.example.album.adapters.ImageSliderAdapter
import com.example.album.databinding.FragmentFullScreenBinding
import com.example.album.model.Hit
import timber.log.Timber

class FullScreenFragment : Fragment() {

    private val TAG= "FullScreenFragment"

    private lateinit var binding: FragmentFullScreenBinding
    private var currentPosition: Int = 0
    private lateinit var viewPager: ViewPager2
    private lateinit var images: ArrayList<Hit>
    private val args : FullScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
    }

    private fun setUpViews(){

        images= args.photoResponse.hits as ArrayList<Hit>
        currentPosition= args.position
        val adapter = ImageSliderAdapter(images)
        viewPager= binding.viewPager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.setCurrentItem(currentPosition, false)
    }

    private fun Activity.transparentStatusBar(it: Boolean) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor =
            if (it) ContextCompat.getColor(
                this,
                android.R.color.transparent
            ) else ContextCompat.getColor(
                this,
                R.color.primary
            )
    }
}