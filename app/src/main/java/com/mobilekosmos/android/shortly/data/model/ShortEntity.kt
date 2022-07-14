package com.mobilekosmos.android.shortly.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ShortEntity(
    @field:Json(name = "code") val name: String,
    @field:Json(name = "short_link") val short_link: String,
    @field:Json(name = "full_short_link") val full_short_link: String,
    @field:Json(name = "short_link2") val short_link2: String,
    @field:Json(name = "full_short_link2") val full_short_link2: String,
    @field:Json(name = "share_link") val share_link: String,
    @field:Json(name = "full_share_link") val full_share_link: String,
    @field:Json(name = "original_link") val original_link: String
) : Parcelable