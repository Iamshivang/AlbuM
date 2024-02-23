package com.example.album.model

data class PhotosResponse(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)