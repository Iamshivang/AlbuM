package com.example.album.ui.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.album.R
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.model.PhotosResponse
import com.example.album.paging.HitPagingAdapter
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var gallerydapter: HitPagingAdapter
    private lateinit var rvGallery: RecyclerView
    private var images: ArrayList<Hit> = ArrayList()
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

    }

    private fun init() {

        requireActivity().transparentStatusBar(true)
    }

    private fun setViews() {
        rvGallery= binding.rvGallery
        paginationProgressBar= binding.progressBarHorizontal
        progressLoading= binding.pbBubbleLoding
        errorProgressBar= binding.lottieError
        gallerydapter= HitPagingAdapter()

        defineState(3)

        rvGallery.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = gallerydapter
            setHasFixedSize(true)
        }

        gallerydapter.setOnClickListener(object: HitPagingAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                // using SafeArg
                val action = HomeFragmentDirections.actionHomeFragmentToFullScreenFragment( position)
                findNavController().navigate(action)
            }
        })

        viewModel.list.observe(viewLifecycleOwner){
            gallerydapter.submitData(lifecycle, it)
        }
    }

    private fun setObservers(){

        viewModel.liveDataList.observe(viewLifecycleOwner){resource ->

            when(resource){
                is Resource.Success -> {

                    defineState(3)

                    resource.data?.let { list->

//                        images= list as ArrayList<Hit>
//                        gallerydapter.differ.submitList(list)
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
                R.color.base2_bg
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