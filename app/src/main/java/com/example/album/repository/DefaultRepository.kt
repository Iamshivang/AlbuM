package com.example.album.repository

import com.example.album.api.PhotosAPI
import com.example.album.model.Hit
import com.example.album.model.PhotosResponse
import com.example.album.utils.Resource
import retrofit2.Response
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val api: PhotosAPI
): MainRepository {
    override suspend fun getPhotos(query: String, colors: String, pageNumber: Int): Resource<List<Hit>>{

        val response: Response<PhotosResponse> =  try {

            api.getPhotos(query, colors, pageNumber)

        } catch (e: Exception){
            return Resource.Error("An ERROR occurred: " + e.message.toString())
        }

        return Resource.Success(response.body()?.hits!!)
    }
}