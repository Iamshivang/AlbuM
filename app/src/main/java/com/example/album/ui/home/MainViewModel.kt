package com.example.album.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.album.model.Hit
import com.example.album.repository.DefaultRepository
import com.example.album.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DefaultRepository
): ViewModel(){

    private val _photos = MutableLiveData<Resource<List<Hit>>>()
    val photos: LiveData<Resource<List<Hit>>> = _photos

    fun fetchPhotos(query: String, colors: String, pageNumber: Int) {
        viewModelScope.launch {
            _photos.value = Resource.Loading()  // Show loading state
            val result = repository.getPhotos(query, colors, pageNumber)
            _photos.value = result
            Log.i("MainViewModel", "Data: ${result}")
        }
    }

//    fun getHitsData(query: String): LiveData<PagingData<Hit>> {
//        return repository.getHits(query).cachedIn(viewModelScope)
//    }
//    val list: LiveData<PagingData<Hit>> = repository.getHits("india").cachedIn(viewModelScope)

    //
    var liveDataList: MutableLiveData<Resource<List<Hit>>> = MutableLiveData()

    private var _lists= MutableLiveData<PagingData<Hit>>()
    val lists: LiveData<PagingData<Hit>> get() = _lists

    fun loadListOfData(query: String, colors: String, pageNumber: Int)= viewModelScope.launch{

        liveDataList.postValue(Resource.Loading())

        try {

            if(hasInternetConnectivity()){

//                val list = repository.getHits("india")
//                liveDataList.postValue(list)
            }else{
                liveDataList.postValue(Resource.Error("No Internet Connection."))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> liveDataList.postValue(Resource.Error("Network Failure."))
                else -> liveDataList.postValue(Resource.Error("Conversion Failure."))
            }
        }

    }

    private fun hasInternetConnectivity(): Boolean{

        // TODO check Internet Connectivity

        return true
    }

}