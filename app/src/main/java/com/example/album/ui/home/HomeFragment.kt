package com.example.album.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.album.R
import com.example.album.adapters.HiltAdapter
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.paging.HitPagingAdapter
import com.example.album.ui.MainActivity
import com.example.album.ui.fullScreenPhoto.FullscreenActivity
import com.example.album.ui.imageViewer.ImagerViewerActivity
import com.example.album.utils.PrefManager
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"
//    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryHiltAdapter: HitPagingAdapter
    private lateinit var rvGallery: RecyclerView
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater)

        viewModel= (activity as MainActivity).viewModel

        rvGallery= binding.rvGallery

        setViews()
        return binding.root
    }


    private fun setViews() {

        defineState(1)

        galleryHiltAdapter= HitPagingAdapter()
        prefManager = PrefManager(requireActivity())
        prefManager.setQuery("Cars")
        val query= prefManager.getQuery()


        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            adapter = galleryHiltAdapter
        }

        viewModel.getHitsData(query).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    defineState(1)
                }
                is Resource.Success -> {
                    // Submit the PagingData to your adapter
                    resource.data?.let { galleryHiltAdapter.submitData(lifecycle, it) }
                    Log.i("HomeFragment", "Data: ${resource.data}")
                    defineState(2)
                }
                is Resource.Error -> {
                    // Display error message
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Error occurred: ${resource.message}")
                    defineState(3)
                }
            }
        }


        galleryHiltAdapter.setOnClickListener(object: HitPagingAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                val intent = Intent(requireActivity(), ImagerViewerActivity::class.java)
                intent.putExtra("selectedImage", model) // Pass only the selected Hit object
                startActivity(intent)

            }
        })

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireActivity(), FullscreenActivity::class.java))
        }
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
}