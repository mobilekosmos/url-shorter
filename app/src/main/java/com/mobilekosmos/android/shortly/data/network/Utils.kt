package com.mobilekosmos.android.shortly.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.mobilekosmos.android.shortly.MyApplication


class Utils {
    companion object {
        // TODO: replace deprecated code.
        fun isNetworkAvailable(): Boolean {
            val connectivityManager = MyApplication.getApplicationContext()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
