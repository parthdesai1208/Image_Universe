package com.parthdesai1208.obvious.view.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.parthdesai1208.imageuniverse.model.ImageResponse

class HomeDataSourceFactory(private val context: Context) : DataSource.Factory<Int, ImageResponse> (){

    val imageLiveDataSource = MutableLiveData<HomeDataSource>()
    override fun create(): DataSource<Int, ImageResponse> {
        val imageDataSource =
            HomeDataSource(context)
        imageLiveDataSource.postValue(imageDataSource)
        return imageDataSource
    }
}