package com.example.album.ui.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.album.R
import com.example.album.adapters.GalleryAdapter
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.model.PhotosResponse
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel  by viewModels()
    private lateinit var gallerydapter: GalleryAdapter
    private lateinit var rvGallery: RecyclerView
    private lateinit var images: ArrayList<Hit>
    private lateinit var progressLoading: LottieAnimationView
    private lateinit var paginationProgressBar: ProgressBar
    private lateinit var errorProgressBar: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setViews()
        setObservers()

    }

    private fun init() {

        requireActivity().transparentStatusBar(true)

        viewModel.loadListOfData("car", "", 1)
    }

    private fun setViews() {
//        insetsController?.isAppearanceLightStatusBars = true
        rvGallery= binding.rvGallery
        paginationProgressBar= binding.progressBarHorizontal
        progressLoading= binding.pbBubbleLoding
        errorProgressBar= binding.lottieError
        gallerydapter= GalleryAdapter()

        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            adapter = gallerydapter
        }

        gallerydapter.setOnClickListener(object: GalleryAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                // using SafeArg
                val action = HomeFragmentDirections.actionHomeFragmentToFullScreenFragment(PhotosResponse(images,0,0), position)
                findNavController().navigate(action)

                /* manually creating bundle
//                val bundle= Bundle().apply {
//                    putParcelable("photo", model)
//                }
//                findNavController().navigate(
//                    R.id.action_homeFragment_to_fullScreenFragment,
//                    bundle
                )  */
            }
        })
    }

    private fun setObservers(){

        viewModel.liveDataList.observe(viewLifecycleOwner){resource ->

            when(resource){
                is Resource.Success -> {

                    defineState(3)

                    resource.data?.let { list->

                        images= list as ArrayList<Hit>
                        gallerydapter.differ.submitList(list)
                        Timber.tag(TAG+ " : Result").d(list.toString())

                    }
                }

                is Resource.Error -> {
                    defineState(5)
                    resource.message?.let {
                        Timber.tag(TAG+ " : Error").e(it)
                    }
                }

                is Resource.Loading -> {
                    defineState(1)
                }
            }
        }
    }

    private fun Activity.transparentStatusBar(it: Boolean) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = ContextCompat.getColor(
                this,
                R.color.red2
            )
    }


    private fun defineState(type: Int){

        if(type== 1){

            // Init1al loading
            progressLoading.isVisible= true
            rvGallery.isVisible= false
            paginationProgressBar.isVisible= false
            errorProgressBar.isVisible= false
            progressLoading.playAnimation()

        }else if (type== 3){

            // when photos are loaded
            rvGallery.isVisible= true
            progressLoading.isVisible= false
            paginationProgressBar.isVisible= false
            errorProgressBar.isVisible= false

        }else if (type== 4){

            // paging progress
            rvGallery.isVisible= true
            paginationProgressBar.isVisible= true
            progressLoading.isVisible= false
            errorProgressBar.isVisible= false

        }else if (type== 5){

            // Error on loading data
            errorProgressBar.isVisible= true
            progressLoading.isVisible= false
            rvGallery.isVisible= false
            paginationProgressBar.isVisible= false
            errorProgressBar.playAnimation()

        }
    }
}