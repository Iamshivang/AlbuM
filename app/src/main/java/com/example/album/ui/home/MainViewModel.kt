package com.example.album.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    //
    val list= repository.getHits("india").cachedIn(viewModelScope)
    //
    var liveDataList: MutableLiveData<Resource<List<Hit>>> = MutableLiveData()

    fun loadListOfData(query: String, colors: String, pageNumber: Int)= viewModelScope.launch{

        liveDataList.postValue(Resource.Loading())

        try {

            if(hasInternetConnectivity()){

//                val list = repository.getPhotos(query, colors, pageNumber)
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