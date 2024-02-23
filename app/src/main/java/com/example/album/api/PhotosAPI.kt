package com.example.album.api

import com.example.album.model.PhotosResponse
import com.example.album.utils.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosAPI {

    @GET("/api/")
    suspend fun getPhotos(
        @Query("q") query: String,
        @Query("colors") colors: String,
        @Query("page") page: Int,
        @Query("key") apiKey: String= Constants.API_KEY,
        @Query("per_page") per_page: Int= Constants.per_page,
        @Query("image_type") imageType: String= Constants.imageType,
        @Query("orientation") orientation: String= Constants.orientation,
        @Query("pretty") pretty: Boolean= Constants.isPretty
    ): Response<PhotosResponse>
}