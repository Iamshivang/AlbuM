package com.example.album.repository

import com.example.album.model.Hit
import com.example.album.utils.Resource

interface MainRepository {

    suspend fun getPhotos(query: String, colors: String, pageNumber: Int): Resource<List<Hit>>
}