package com.parthdesai1208.imageuniverse.model.repo

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.parthdesai1208.imageuniverse.model.ImageResponse

data class RepoResponse(
    val list: LiveData<PagedList<ImageResponse>>,
    val networkErrors: LiveData<String>,
    val imageList: LiveData<ArrayList<ImageResponse>>,
    val refresh: () -> Unit
    )