package com.example.album.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.album.dataBase.dao.HitDataDao
import com.example.album.model.Hit

/*
created by  Shivang Yadav on 22-02-2025
gitHub: https://github.com/Iamshivang
project: AlbuM
description:
*/


@Database(entities = [Hit::class], version = 1)
abstract class AppDatabase: RoomDatabase(){

    abstract fun hitDataDao(): HitDataDao

    companion object {
        fun getInstance(context: Context): AppDatabase  {

            return Room.databaseBuilder(context, AppDatabase::class.java, "hit.db")
                .allowMainThreadQueries()
                .build()
        }
    }
}