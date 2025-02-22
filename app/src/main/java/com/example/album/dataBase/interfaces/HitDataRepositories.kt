package com.example.album.dataBase.interfaces

import com.example.album.model.Hit

/*
created by  Shivang Yadav on 22-02-2025
gitHub: https://github.com/Iamshivang
project: AlbuM
description:
*/


interface HitDataRepositories {

    suspend fun getHitList(): List<Hit>?

    suspend fun insertHit(hit: Hit)

    suspend fun deleteHitById(hitId: Int)
}