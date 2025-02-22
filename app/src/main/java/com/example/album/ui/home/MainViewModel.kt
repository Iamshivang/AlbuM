package com.example.album.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.album.dataBase.interfaces.HitDataRepositories
import com.example.album.model.Hit
import com.example.album.repository.DefaultRepository
import com.example.album.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.Call
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DefaultRepository,
    private val collectionRepo: HitDataRepositories
): ViewModel(){

//    val list= repository.getHits("india").cachedIn(viewModelScope)

    fun getHitsData(query: String): LiveData<Resource<PagingData<Hit>>>{

        Resource.Loading<PagingData<Hit>>()

        return repository.getHitsFlow(query)
            .cachedIn(viewModelScope)
            .map<PagingData<Hit>, Resource<PagingData<Hit>>> { pagingData ->
                Resource.Success(pagingData)
            }
            .onStart { emit(Resource.Loading<PagingData<Hit>>()) }
            .catch { e ->
                emit(Resource.Error("Error: ${e.message}"))
            }
            .asLiveData()
    }

    fun insertHit(hit: Hit) {
        viewModelScope.launch {
            try {
                collectionRepo.insertHit(hit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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