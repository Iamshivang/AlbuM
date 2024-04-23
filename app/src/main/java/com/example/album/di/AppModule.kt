package com.example.album.di

import android.app.Application
import com.example.album.api.PhotosAPI
import com.example.album.repository.DefaultRepository
import com.example.album.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePhotosApi(): PhotosAPI = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PhotosAPI::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(api: PhotosAPI, app: Application): DefaultRepository = DefaultRepository(api, app)
}