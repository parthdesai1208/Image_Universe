package com.parthdesai1208.imageuniverse.model.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder


import androidx.paging.PagedList
import com.parthdesai1208.imageuniverse.model.ImageResponse
import com.parthdesai1208.obvious.utils.PAGE_SIZE
import com.parthdesai1208.obvious.view.home.HomeDataSourceFactory

class HomeRepository(
    private val mContext: Context
) {


    fun getImageUsingPaging(): RepoResponse {

        val itemDataSourceFactory =
            HomeDataSourceFactory(mContext)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(PAGE_SIZE)
            .build()
        val livePagedList: LiveData<PagedList<ImageResponse>> =
            LivePagedListBuilder(itemDataSourceFactory, config).build()
        val networkError = Transformations.switchMap(itemDataSourceFactory.imageLiveDataSource) {
            it.networkError
        }
        val imageList = Transformations.switchMap(itemDataSourceFactory.imageLiveDataSource) {
            it.imageList
        }

        return RepoResponse(
            livePagedList,
            networkError,
            imageList
        ) {
            itemDataSourceFactory.imageLiveDataSource.value?.invalidate()
        }
    }
}