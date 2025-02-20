//package com.example.album.paging
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.album.api.PhotosAPI
//import com.example.album.model.Hit
//import com.example.album.utils.Constants.STARTING_PAGE_INDEX
//
//class HitPagingSource (private val photosApi: PhotosAPI,
//                       private val query: String
//): PagingSource<Int, Hit>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hit> {
//
//        return try {
//
//            val position= params.key?: STARTING_PAGE_INDEX
//            val response= photosApi.getPhotos(query, position)
//            LoadResult.Page(
//                response.hits,
//                if(position == STARTING_PAGE_INDEX) null else position- 1,
//                if(position == response.totalHits) null else position+ 1
//            )
//        }catch (e : Exception){
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Hit>): Int? {
//
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//}