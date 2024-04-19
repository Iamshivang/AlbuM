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
    val imageURL: String?= null,
    val imageWidth: Int?,
    val largeImageURL: String?= null,
    val likes: Int?,
    val pageURL: String?= null,
    val previewHeight: Int?,
    val previewURL: String?= null,
    val previewWidth: Int?,
    val tags: String?= null,
    val user: String?= null,
    val user_id: Int?,
    val views: Int?,
    val webformatHeight: Int?,
    val webformatWidth: Int?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(collections)
        parcel.writeValue(comments)
        parcel.writeValue(downloads)
        parcel.writeValue(id)
        parcel.writeValue(imageHeight)
        parcel.writeValue(imageSize)
        parcel.writeString(imageURL)
        parcel.writeValue(imageWidth)
        parcel.writeString(largeImageURL)
        parcel.writeValue(likes)
        parcel.writeString(pageURL)
        parcel.writeValue(previewHeight)
        parcel.writeString(previewURL)
        parcel.writeValue(previewWidth)
        parcel.writeString(tags)
        parcel.writeString(user)
        parcel.writeValue(user_id)
        parcel.writeValue(views)
        parcel.writeValue(webformatHeight)
        parcel.writeValue(webformatWidth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Hit> {
        override fun createFromParcel(parcel: Parcel): Hit {
            return Hit(parcel)
        }

        override fun newArray(size: Int): Array<Hit?> {
            return arrayOfNulls(size)
        }
    }
}