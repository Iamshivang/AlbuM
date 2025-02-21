package com.example.album.ui.trending

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.album.R
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.databinding.FragmentTrendingBinding
import com.example.album.model.Hit
import com.example.album.paging.HitPagingAdapter
import com.example.album.ui.MainActivity
import com.example.album.ui.fullScreenPhoto.FullscreenActivity
import com.example.album.ui.home.MainViewModel
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingFragment : Fragment() {

    private val TAG= "TrendingFragment"
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTrendingBinding
    private lateinit var galleryHiltAdapter: HitPagingAdapter
    private lateinit var rvGallery: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentTrendingBinding.inflate(inflater)

        viewModel= (activity as MainActivity).viewModel

        rvGallery= binding.rvGallery

        setViews()
        return binding.root
    }


    private fun setViews() {

        defineState(1)

        galleryHiltAdapter= HitPagingAdapter()


        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = galleryHiltAdapter
        }

        viewModel.getHitsData("trending").observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    defineState(1)
                }
                is Resource.Success -> {
                    // Submit the PagingData to your adapter
                    resource.data?.let { galleryHiltAdapter.submitData(lifecycle, it) }
                    Log.i(TAG, "Data: ${resource.data}")
                    defineState(2)
                }
                is Resource.Error -> {
                    // Display error message
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error occurred: ${resource.message}")
                    defineState(3)
                }
            }
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

        galleryHiltAdapter.setOnClickListener(object: HitPagingAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                val intent = Intent(requireActivity(), FullscreenActivity::class.java)
                intent.putExtra("position", position)
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
}