package com.example.album.model

import java.util.ArrayList

data class PhotosResponse(
    val hits: ArrayList<Hit>,
    val total: Int,
    val totalHits: Int
)