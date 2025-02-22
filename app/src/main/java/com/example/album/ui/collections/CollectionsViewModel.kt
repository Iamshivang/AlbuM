package com.example.album.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.album.dataBase.interfaces.HitDataRepositories
import com.example.album.model.Hit
import com.example.album.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
created by  Shivang Yadav on 22-02-2025
gitHub: https://github.com/Iamshivang
project: AlbuM
description:
*/

@HiltViewModel
class CollectionsViewModel  @Inject constructor(
    private val repository: HitDataRepositories
): ViewModel(){

    private val _hitList = MutableLiveData<Resource<List<Hit>>>()
    val hitList: LiveData<Resource<List<Hit>>> get() = _hitList

    fun getAllHits() {
        viewModelScope.launch {
            _hitList.postValue(Resource.Loading()) // Set loading state inside coroutine
            try {
                val hits = repository.getHitList()
                if (!hits.isNullOrEmpty()) {
                    _hitList.postValue(Resource.Success(hits)) // Success
                } else {
                    _hitList.postValue(Resource.Error("No data found")) // Empty case
                }
            } catch (e: Exception) {
                _hitList.postValue(Resource.Error("Failed to fetch hits: ${e.message}"))
            }
        }
    }


    fun deleteHit(hitId: Int) {
        _hitList.value = Resource.Loading()
        viewModelScope.launch {
            try {
                repository.deleteHitById(hitId)
                getAllHits() // Refresh list after deletion
            } catch (e: Exception) {
                _hitList.postValue(Resource.Error("Failed to delete hit: ${e.message}"))
            }
        }
    }

    fun insertHit(hit: Hit) {
        _hitList.value = Resource.Loading()
        viewModelScope.launch {
            try {
                repository.insertHit(hit)
                getAllHits() // Refresh list after deletion
            } catch (e: Exception) {
                _hitList.postValue(Resource.Error("Failed to insert hit: ${e.message}"))
            }
        }
    }
}