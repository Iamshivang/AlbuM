package com.example.album.repository

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.album.api.PhotosAPI
import com.example.album.model.Hit
import com.example.album.model.PhotosResponse
import com.example.album.utils.Constants.PER_PAGE
import com.example.album.utils.Constants.TOTAL_PAGES_TO_LOAD
import com.example.album.utils.Resource
import retrofit2.Response
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val api: PhotosAPI,
    private val appContext: Application
){

    suspend fun getPhotos(query: String, colors: String, pageNumber: Int): Resource<List<Hit>>{

        val response: Response<PhotosResponse> =  try {

            api.getPhotos(query, pageNumber)

        } catch (e: Exception){
            return Resource.Error("An ERROR occurred: " + e.message.toString())
        }

        Log.i("DefaultRepository", "Data: ${response.body()?.hits!!}")
        return Resource.Success(response.body()?.hits!!,  "Photos fetched successfully")
    }

//    fun getHits(query: String)= Pager(
//    config = PagingConfig( pageSize = PER_PAGE, maxSize = TOTAL_PAGES_TO_LOAD, enablePlaceholders = false),
//    pagingSourceFactory = { HitPagingSource(api, query)}
//    ).liveData

    fun downloadFile(username: String, url: String): Long {

        val downloadManager= appContext.getSystemService(DownloadManager::class.java)

        val resquest= DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("$username.jpg")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$username.jpg")

        return downloadManager.enqueue(resquest)
    }
}