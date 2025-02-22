package com.example.album.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.album.model.Hit

/*
created by  Shivang Yadav on 22-02-2025
gitHub: https://github.com/Iamshivang
project: AlbuM
description:
*/


@Dao
interface HitDataDao {

    @Query("SELECT * FROM Hit_Table")
    suspend fun getAllHits(): List<Hit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHit(hit: Hit)

    @Query("DELETE FROM Hit_Table WHERE id = :hitId")
    suspend fun deleteHitById(hitId: Int)

}