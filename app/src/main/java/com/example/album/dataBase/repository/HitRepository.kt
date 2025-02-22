package com.example.album.dataBase.repository

import android.util.Log
import com.example.album.dataBase.AppDatabase
import com.example.album.dataBase.interfaces.HitDataRepositories
import com.example.album.model.Hit
import javax.inject.Inject

/*
created by  Shivang Yadav on 22-02-2025
gitHub: https://github.com/Iamshivang
project: AlbuM
description:
*/


class HitRepository @Inject  constructor(
    private val db: AppDatabase
): HitDataRepositories {

    override suspend fun getHitList(): List<Hit>? {

        return try {
            db.hitDataDao().getAllHits()
        } catch (e: Exception) {
            Log.e("HitRepository", "Error fetching hit list: ${e.message}", e)
            null
        }
    }

    override suspend fun insertHit(hit: Hit) {

        try {
            db.hitDataDao().insertHit(hit)
        } catch (e: Exception) {
            Log.e("HitRepository", "Error inserting hit: ${e.message}", e)
        }
    }

    override suspend fun deleteHitById(hitId: Int) {
        try {
            db.hitDataDao().deleteHitById(hitId)
        } catch (e: Exception) {
            Log.e("HitRepository", "Error deleting hit with ID $hitId: ${e.message}", e)
        }
    }
}