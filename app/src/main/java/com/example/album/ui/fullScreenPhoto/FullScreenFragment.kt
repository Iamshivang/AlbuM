package com.example.album.ui.fullScreenPhoto

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.album.R
import com.example.album.adapters.ImageSliderAdapter
import com.example.album.databinding.FragmentFullScreenBinding
import com.example.album.databinding.MoreBottomsheetBinding
import com.example.album.databinding.SetAsBottomsheetBinding
import com.example.album.model.Hit
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class FullScreenFragment : Fragment() {

    private val TAG= "FullScreenFragment"

    private lateinit var binding: FragmentFullScreenBinding
    private lateinit var setASBottomSheetBinding: SetAsBottomsheetBinding
    private lateinit var moreBottomSheetBinding: MoreBottomsheetBinding
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

        requireActivity().transparentStatusBar(true)
        images= args.photoResponse.hits!!
        currentPosition= args.position
        val adapter = ImageSliderAdapter(images)
        viewPager= binding.viewPager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.setCurrentItem(currentPosition, false)

        // registering for page change callback
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    //update the image number textview
//                    binding.imageNumberTV.text = "${position + 1} / 4"
                    currentPosition= position

                }
            }
        )


        adapter.setOnMoreClickListener(object : ImageSliderAdapter.OnMoreClickListener {
            override fun onMoreClick(position: Int, model: Hit) {

                moreBottomDialog()
            }
        })

        adapter.setOnSetAsClickListener(object : ImageSliderAdapter.OnSetAsClickListener {
            override fun onSetAsClick(position: Int, model: Hit) {

                setAsBottomDialog(model)
            }
        })
    }

    private fun setAsBottomDialog(model: Hit){
        setASBottomSheetBinding = SetAsBottomsheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireActivity())



        setASBottomSheetBinding.rlHome.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 1)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlLock.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 0)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlBoth.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 2)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(setASBottomSheetBinding.root)
        dialog.show()
    }

    private fun moreBottomDialog(){
        moreBottomSheetBinding = MoreBottomsheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireActivity())

        moreBottomSheetBinding.rlShare.setOnClickListener {
            Toast.makeText(requireActivity(), "share", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        moreBottomSheetBinding.rlDownload.setOnClickListener {
            Toast.makeText(requireActivity(), "DOWNLOADING", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        moreBottomSheetBinding.rlAdd.setOnClickListener {
            Toast.makeText(requireActivity(), "Share", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        moreBottomSheetBinding.rlClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(moreBottomSheetBinding.root)
        dialog.show()
    }

    private fun setWallpaperFromUrl(imageUrl: String, screen: Int) {

        GlobalScope.launch(Dispatchers.IO){

            Glide.with(binding.root)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            val wallpaperManager = WallpaperManager.getInstance(context)
                            if (screen== 1) {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                            } else if(screen== 0){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            } else if(screen== 2){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            }

                            Toast.makeText(requireActivity(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Timber.tag(TAG).e("Failed to set wallpaper: ${e.message}")
                            Toast.makeText(requireActivity(), "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Do nothing
                    }
                })
        }
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

    override fun onDestroy() {
        super.onDestroy()

        // unregistering the onPageChangedCallback
        viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }
}