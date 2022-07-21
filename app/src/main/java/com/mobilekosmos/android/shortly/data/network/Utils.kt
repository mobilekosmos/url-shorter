package com.mobilekosmos.android.shortly.data.network

import android.content.Context
import android.net.ConnectivityManager


class Utils() {
    companion object {
        // TODO: replace deprecated code.
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
