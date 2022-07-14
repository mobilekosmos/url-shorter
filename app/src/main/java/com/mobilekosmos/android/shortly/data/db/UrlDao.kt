package com.mobilekosmos.android.shortly.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the ShortURL class.
 */
@Dao
interface UrlDao {
    @Query("SELECT * from short_urls")
    fun getShortenedURLHistory(): Flow<List<ShortURLEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewURLToHistory(url: ShortURLEntity)
}
