package com.example.album.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class PhotosResponse(
    val hits: ArrayList<Hit>?,
    val total: Int?,
    val totalHits: Int?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Hit),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(hits)
        parcel.writeValue(total)
        parcel.writeValue(totalHits)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotosResponse> {
        override fun createFromParcel(parcel: Parcel): PhotosResponse {
            return PhotosResponse(parcel)
        }

        override fun newArray(size: Int): Array<PhotosResponse?> {
            return arrayOfNulls(size)
        }
    }
}