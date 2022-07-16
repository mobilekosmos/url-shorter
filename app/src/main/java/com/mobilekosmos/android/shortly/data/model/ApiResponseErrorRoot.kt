package com.mobilekosmos.android.shortly.data.model

data class ApiResponseErrorRoot(
    val ok: Boolean,
    val error_code: Int,
    val error: String
)