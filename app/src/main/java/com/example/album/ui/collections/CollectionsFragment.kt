package com.example.album.ui.collections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.album.R
import com.example.album.adapters.HiltAdapter
import com.example.album.databinding.FragmentCollectionsBinding
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.paging.HitPagingAdapter
import com.example.album.ui.MainActivity
import com.example.album.ui.fullScreenPhoto.FullscreenActivity
import com.example.album.ui.home.MainViewModel
import com.example.album.ui.imageViewer.ImagerViewerActivity
import com.example.album.utils.PrefManager
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment : Fragment() {

    private val TAG= "CollectionsFragment"
    private val viewModel: CollectionsViewModel by viewModels()
    private lateinit var binding: FragmentCollectionsBinding
    private lateinit var galleryHiltAdapter: HiltAdapter
    private lateinit var rvGallery: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCollectionsBinding.inflate(inflater)


        rvGallery= binding.rvGallery

        setViews()
        return binding.root
    }

    private fun setViews() {

        defineState(1)
        galleryHiltAdapter= HiltAdapter()


        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            adapter = galleryHiltAdapter
        }

        viewModel.getAllHits()

        viewModel.hitList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    defineState(1) // Show loading state
                }
                is Resource.Success -> {
                    resource.data?.let {
                        galleryHiltAdapter.differ.submitList(it) // Submit data to adapter
                    }
                    Log.i(TAG, "Data: ${resource.data}")
                    defineState(2) // Show success state
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error occurred: ${resource.message}")
                    defineState(3) // Show error state
                }
            }
        }

        galleryHiltAdapter.setOnClickListener(object: HiltAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                val intent = Intent(requireActivity(), ImagerViewerActivity::class.java)
                intent.putExtra("selectedImage", model) // Pass only the selected Hit object
                intent.putExtra("isCollectionFrag", true) // Pass only the selected Hit object
                startActivity(intent)

            }
        })
    }

    private fun defineState(type: Int){

        if(type== 1){

            // Init1al loading
            binding.progressBarHorizontal.isVisible= true
            rvGallery.isVisible= false
            binding.llShimmer.isVisible= true
//            errorProgressBar.isVisible= false

        }else if (type== 2){

            // when photos are loaded
            binding.progressBarHorizontal.isVisible= false
            rvGallery.isVisible= true
            binding.llShimmer.isVisible= false
//            errorProgressBar.isVisible= false

        }else if (type== 3){

            // Error on loading data
            binding.progressBarHorizontal.isVisible= false
            rvGallery.isVisible= true
            binding.llShimmer.isVisible= false
//            errorProgressBar.isVisible= false
        }
    }

    override fun onResume() {
        super.onResume()
        setViews()
    }

}