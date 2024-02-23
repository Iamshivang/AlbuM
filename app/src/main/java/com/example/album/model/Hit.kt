package com.example.album.model

import android.os.Parcel
import android.os.Parcelable

data class Hit(
    val collections: Int?,
    val comments: Int?,
    val downloads: Int?,
    val id: Int?,
    val imageHeight: Int?,
    val imageSize: Int?,
    val imageWidth: Int?,
    val largeImageURL: String?= null,
    val likes: Int?,
    val pageURL: String?= null,
    val previewHeight: Int?,
    val previewURL: String?= null,
    val previewWidth: Int?,
    val tags: String?= null,
    val type: String?= null,
    val user: String?= null,
    val userImageURL: String?= null,
    val user_id: Int?,
    val views: Int?,
    val webformatHeight: Int?,
    val webformatURL: String?= null,
    val webformatWidth: Int?
)