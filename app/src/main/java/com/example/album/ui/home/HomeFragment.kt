package com.example.album.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.album.R
import com.example.album.adapters.GalleryAdapter
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.ui.MainViewModel
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"

    private lateinit var binding: FragmentHomeBinding
    val viewModel: MainViewModel  by viewModels()
    private lateinit var gallerydapter: GalleryAdapter
    lateinit var rvGallery: RecyclerView
    lateinit var progressBar: LottieAnimationView
    lateinit var paginationProgressBar: ProgressBar

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
        viewModel.loadListOfData("india", "", 1)
    }

    private fun setViews() {
//        insetsController?.isAppearanceLightStatusBars = true
        gallerydapter= GalleryAdapter()
        rvGallery= binding.rvGallery
        paginationProgressBar= binding.progressBarHorizontal
        progressBar= binding.pbBubbleLoding

        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            adapter = gallerydapter
        }

        gallerydapter.setOnClickListener(object: GalleryAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                // using SafeArg
                val action = HomeFragmentDirections.actionHomeFragmentToFullScreenFragment()
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

        viewModel.liveDataList.observe(requireActivity(), Observer {resource ->

            when(resource){
                is Resource.Success -> {
//                    hideProgressBar()
                    resource.data?.let { list->
//                        newsAdapter.differ.submitList(newsResponse.articles)
//                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2 // reason pf 2: exclude last page and round off of division
//                        isLastPage = viewModel.breakingNewsPage == totalPages
//                        if(isLastPage) {
//                            rvBreakingNews.setPadding(0, 0, 0, 0)
//                        }


                        binding.rvGallery.isVisible= true
                        gallerydapter.differ.submitList(list as ArrayList<Hit>)



                        Log.e("check", list.toString())

                    }
                }

                is Resource.Error -> {
//                    hideProgressBar()
                    resource.message?.let {
                        Log.e(TAG, "An Error occurred: $it")
                        Toast.makeText(requireContext(), "Error Occurred: ${it.toString()}", Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
//                    showProgressBar()
                }
            }
        })

    }
}