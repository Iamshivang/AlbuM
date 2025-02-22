package com.example.album.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.album.api.PhotosAPI
import com.example.album.dataBase.AppDatabase
import com.example.album.dataBase.interfaces.HitDataRepositories
import com.example.album.dataBase.repository.HitRepository
import com.example.album.repository.DefaultRepository
import com.example.album.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build()

        return db;
    }

    @Provides
    @Singleton
    fun provideHitDataDao(db: AppDatabase) = db.hitDataDao()

    @Provides
    @Singleton
    fun providePatientDataRepository(
        db: AppDatabase
    ): HitDataRepositories {
        return HitRepository(db)
    }
}