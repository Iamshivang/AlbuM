package com.example.album.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.album.databinding.ActivitySearchBinding
import com.example.album.model.Hit
import com.example.album.paging.HitPagingAdapter
import com.example.album.ui.imageViewer.ImagerViewerActivity
import com.example.album.ui.home.MainViewModel
import com.example.album.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val TAG = "SearchActivity"
    private val viewModel: MainViewModel by viewModels()
    private lateinit var galleryHiltAdapter: HitPagingAdapter
    private lateinit var rvGallery: RecyclerView

    private val searchQuery = MutableLiveData<String>()
    private var searchJob: Job? = null  // To prevent multiple API calls while typing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViews()
        observeSearchQuery()
    }

    private fun setViews() {

        rvGallery = binding.rvGallery

        galleryHiltAdapter = HitPagingAdapter()

        binding.backBtn.setOnClickListener{
            this.onBackPressed()
        }


        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = galleryHiltAdapter
        }

        // Add TextWatcher for live search
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()  // Cancel previous search job to avoid multiple API calls
                searchJob = lifecycleScope.launch {
                    delay(1000) // Debounce time to avoid excessive API calls
                    searchQuery.value = s.toString().trim()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        galleryHiltAdapter.setOnClickListener(object : HitPagingAdapter.OnClickListener {
            override fun onCLick(position: Int, model: Hit) {
                val intent = Intent(this@SearchActivity, ImagerViewerActivity::class.java)
                intent.putExtra("selectedImage", model) // Pass only the selected Hit object
                startActivity(intent)
            }
        })
    }

    private fun observeSearchQuery() {
        searchQuery.observe(this) { query ->
            if (query.isNotEmpty()) {
                fetchSearchResults(query)
            }
        }
    }

    private fun fetchSearchResults(query: String) {
        viewModel.getHitsData(query).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> defineState(1)
                is Resource.Success -> {
                    resource.data?.let { galleryHiltAdapter.submitData(lifecycle, it) }
                    Log.i(TAG, "Data: ${resource.data}")
                    defineState(2)
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error occurred: ${resource.message}")
                    defineState(3)
                }
            }
        }
    }

    private fun defineState(type: Int) {
        when (type) {
            1 -> { // Initial loading
                binding.progressBarHorizontal.isVisible = true
                rvGallery.isVisible = false
                binding.llShimmer.isVisible = true
            }
            2 -> { // Data loaded
                binding.progressBarHorizontal.isVisible = false
                rvGallery.isVisible = true
                binding.llShimmer.isVisible = false
            }
            3 -> { // Error state
                binding.progressBarHorizontal.isVisible = false
                rvGallery.isVisible = false
                binding.llShimmer.isVisible = false
            }
        }
    }
}
