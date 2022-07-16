package com.mobilekosmos.android.shortly.data.db

import androidx.room.*
import com.mobilekosmos.android.shortly.data.model.ShortURLEntity
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the ShortURL class.
 */
@Dao
interface UrlDao {
//    @Query("SELECT * from short_urls ORDER BY uniqueId DESC")
    @Query("SELECT * from short_urls")
    fun getShortenedURLHistory(): Flow<List<ShortURLEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewURLToHistory(url: ShortURLEntity)

    @Delete
    suspend fun deleteFromHistory(url: ShortURLEntity)
}
