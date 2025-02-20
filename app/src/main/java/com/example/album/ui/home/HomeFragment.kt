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
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"
//    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryHiltAdapter: HiltAdapter
    private lateinit var rvGallery: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater)

        viewModel= (activity as MainActivity).viewModel

        init()
        setViews()
        setObservers()
        return binding.root
    }

    private fun init() {

        rvGallery= binding.rvGallery
        defineState(1)
        viewModel.fetchPhotos("India", "", 1)
    }

    private fun setViews() {

        galleryHiltAdapter= HiltAdapter()
//        paginationProgressBar= binding.progressBarHorizontal
//        progressBar= binding.pbBubbleLoding

        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            adapter = galleryHiltAdapter
        }

//        gallerydapter.setOnClickListener(object: GalleryAdapter.OnClickListener{
//            override fun onCLick(position: Int, model: Hit) {
//
//                // using SafeArg
//                val action = HomeFragmentDirections.actionHomeFragmentToFullScreenFragment()
//                findNavController().navigate(action)
//
//                /* manually creating bundle
////                val bundle= Bundle().apply {
////                    putParcelable("photo", model)
////                }
////                findNavController().navigate(
////                    R.id.action_homeFragment_to_fullScreenFragment,
////                    bundle
//                )  */
//            }



//        viewModel._list.observe(viewLifecycleOwner){
//            galleryAdapter.submitData(lifecycle, it)
//        }

//        galleryAdapter.setOnClickListener(object: HitPagingAdapter.OnClickListener{
//            override fun onCLick(position: Int, model: Hit) {
//
//                val intent = Intent(requireActivity(), FullscreenActivity::class.java)
//                intent.putExtra("position", position)
//                startActivity(intent)
//
//            }
//        })
    }

    private fun setObservers(){

        viewModel.photos.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {

                    defineState(1)
                }
                is Resource.Success -> {

                    galleryHiltAdapter.differ.submitList(result.data ?: emptyList())// Update adapter
                    Log.i("HomeFragment", "Data: ${result.data}")
                    defineState(2)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Error occurred: ${result.message}")
                }
            }
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