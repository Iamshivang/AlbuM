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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.album.R
import com.example.album.databinding.FragmentFullScreenBinding
import timber.log.Timber

class FullScreenFragment : Fragment() {

    private val TAG= "FullScreenFragment"

    private lateinit var binding: FragmentFullScreenBinding
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

        val hit= args.hit

        hit.largeImageURL?.let {
            try {

                Glide
                    .with(binding.root)
                    .load(it)
                    .fitCenter()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            TODO("Not yet implemented")
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
//                            itemBinding.ivGalleryPhoto.visibility= View.VISIBLE
//                            itemBinding.sflPlaceholder.visibility= View.GONE
                            requireActivity().transparentStatusBar(true)
                            return false
                        }
                    }).into(binding.pvFullscreen)

            }catch (e: Exception){

                Timber.tag(TAG).e("An Error occurred: %s", e)
            }
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
}