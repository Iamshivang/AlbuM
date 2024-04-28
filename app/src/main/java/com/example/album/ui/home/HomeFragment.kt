package com.example.album.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.album.R
import com.example.album.databinding.FragmentHomeBinding
import com.example.album.model.Hit
import com.example.album.paging.HitPagingAdapter
import com.example.album.ui.MainActivity
import com.example.album.ui.fullScreenPhoto.FullscreenActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG= "HomeFragment"
//    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var gallerydapter: HitPagingAdapter
    private lateinit var rvGallery: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel= (activity as MainActivity).viewModel

        setViews()
    }

    private fun setViews() {

        rvGallery= binding.rvGallery
//        paginationProgressBar= binding.progressBarHorizontal
//        progressLoading= binding.pbBubbleLoding
//        errorProgressBar= binding.lottieError
        gallerydapter= HitPagingAdapter()

        rvGallery.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = gallerydapter
            setHasFixedSize(true)
        }

        viewModel.list.observe(viewLifecycleOwner){
            gallerydapter.submitData(lifecycle, it)
        }

        gallerydapter.setOnClickListener(object: HitPagingAdapter.OnClickListener{
            override fun onCLick(position: Int, model: Hit) {

                val intent = Intent(requireActivity(), FullscreenActivity::class.java)
                intent.putExtra("position", position)
                startActivity(intent)

            }
        })
    }

}