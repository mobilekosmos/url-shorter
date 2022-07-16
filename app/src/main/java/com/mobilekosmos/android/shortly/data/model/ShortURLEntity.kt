/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilekosmos.android.shortly.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "short_urls")
data class ShortURLEntity(
//    @PrimaryKey @ColumnInfo(name = "id") val original_link: String,
//    @PrimaryKey(autoGenerate = true) val uniqueId : Int,
    @PrimaryKey @field:Json(name = "code") val code: String,
    @field:Json(name = "short_link") val short_link: String,
    @field:Json(name = "full_short_link") val full_short_link: String,
    @field:Json(name = "short_link2") val short_link2: String,
    @field:Json(name = "full_short_link2") val full_short_link2: String,
    @field:Json(name = "share_link") val share_link: String,
    @field:Json(name = "full_share_link") val full_share_link: String,
    @field:Json(name = "original_link") val original_link: String
) {
    override fun toString() = original_link
}