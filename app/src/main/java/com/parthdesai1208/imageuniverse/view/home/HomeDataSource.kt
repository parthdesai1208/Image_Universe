package com.parthdesai1208.obvious.view.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.parthdesai1208.imageuniverse.model.ImageResponse

import com.parthdesai1208.obvious.utils.PAGE_SIZE
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.ArrayList

class HomeDataSource(private val mContext: Context) :
    PageKeyedDataSource<Int, ImageResponse>() {
    private var listSize: Int = 0
    private val list = ArrayList<ImageResponse>()
    private val _imageList = MutableLiveData<ArrayList<ImageResponse>>()

     val imageList: LiveData<ArrayList<ImageResponse>>
        get() = _imageList
    private val _networkError = MutableLiveData<String>()

    val networkError: LiveData<String>
        get() = _networkError


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ImageResponse>
    ) {
        val json: String
        list.clear()
        try {
            val `is`: InputStream = mContext.assets.open("data.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            val byteSize = `is`.read(buffer)
            if (byteSize == -1) { //if its end of the stream close stream
                `is`.close()
            }
            json = String(buffer, StandardCharsets.UTF_8)
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val songResponse =
                    ImageResponse(
                        if (jsonObject.has("copyright")) jsonObject.getString("copyright") else "",
                        jsonObject.getString("date"),
                        jsonObject.getString("explanation"),
                        jsonObject.getString("hdurl"),
                        jsonObject.getString("media_type"),
                        jsonObject.getString("service_version"),
                        jsonObject.getString("title"),
                        jsonObject.getString("url")
                    )
                list.add(songResponse)
            }
            list.reverse() //to get latest first
            listSize = list.size
            _imageList.postValue(list)

            var nextSize: Int? = null
            if (list.size < PAGE_SIZE) {
                callback.onResult(list, null, nextSize)
            } else {
                nextSize = PAGE_SIZE
                val tempList: ArrayList<ImageResponse> = ArrayList()
                for (i in 0 until PAGE_SIZE) {
                    tempList.add(list[i])
                }
                callback.onResult(tempList, null, nextSize)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            _networkError.postValue(e.localizedMessage)
        } catch (e: JSONException) {
            e.printStackTrace()
            _networkError.postValue(e.localizedMessage)
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageResponse>) {
        var nextSize: Int? = null
        if (listSize > params.key) {
            if (listSize - params.key <= PAGE_SIZE) {
                val tempList: ArrayList<ImageResponse> = ArrayList()
                for (i in params.key until params.key + (listSize - params.key)) {
                    tempList.add(list[i])
                }
                callback.onResult(tempList, nextSize)
            } else {
                nextSize = params.key + PAGE_SIZE
                val tempList: ArrayList<ImageResponse> = ArrayList()
                for (i in params.key until nextSize) {
                    tempList.add(list[i])
                }
                callback.onResult(tempList, nextSize)
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageResponse>) {
        // ignored, since we only ever append to our initial load
    }

}