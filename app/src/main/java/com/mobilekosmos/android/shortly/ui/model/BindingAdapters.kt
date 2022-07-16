package com.mobilekosmos.android.shortly.ui.model

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "clubsList")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, clubsList: Any?) {
    view.visibility = if (clubsList != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError2")
fun hideIfNetworkError2(view: View, isNetWorkError: Boolean) {
    view.visibility = View.GONE

    if (isNetWorkError) {
        view.visibility = View.VISIBLE
    }
}