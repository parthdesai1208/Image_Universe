package com.parthdesai1208.obvious.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.parthdesai1208.imageuniverse.R


const val PAGE_SIZE = 8

fun circularProgressDrawable(context: Context): RequestOptions {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()


    return RequestOptions()
        .placeholder(circularProgressDrawable)
        .error(R.drawable.ic_error)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        //  .skipMemoryCache(true)
        .fitCenter()
}


fun Context.isInternetAvailable(): Boolean {

    val connMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= 23) {
        val network = connMgr.activeNetwork

        val activeNetwork = connMgr.getNetworkCapabilities(network) ?: return false

        return when {

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

            else -> false

        }
    } else {
        val cm =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

}

fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / columnWidthDp + 0.5).toInt()
}

fun makeUpDate(input: String): String {
    val temp = input.split("-")
    val year = temp[0]
    val month = temp[1]
    val date = temp[2]
    val monthByWord: String?
    when (month) {
        "01" -> {
            monthByWord = "Jan"
        }
        "02" -> {
            monthByWord = "Feb"
        }
        "03" -> {
            monthByWord = "Mar"
        }
        "04" -> {
            monthByWord = "Apr"
        }
        "05" -> {
            monthByWord = "May"
        }
        "06" -> {
            monthByWord = "June"
        }
        "07" -> {
            monthByWord = "July"
        }
        "08" -> {
            monthByWord = "Aug"
        }
        "09" -> {
            monthByWord = "Sep"
        }
        "10" -> {
            monthByWord = "Oct"
        }
        "11" -> {
            monthByWord = "Nov"
        }
        "12" -> {
            monthByWord = "Dec"
        }
        else -> {
            monthByWord = "Dec"
        }
    }

    return "$monthByWord $date, $year"
}

