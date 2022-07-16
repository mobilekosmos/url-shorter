package com.mobilekosmos.android.shortly.data.repository

import android.util.Log
import com.mobilekosmos.android.shortly.data.db.UrlDao
import com.mobilekosmos.android.shortly.data.model.ShortURLEntity
import com.mobilekosmos.android.shortly.data.model.ShortURLEntityRoot
import com.mobilekosmos.android.shortly.data.network.ShortenApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response

@ExperimentalCoroutinesApi
class URLsRepository private constructor(
    private val urlDao: UrlDao,
    private val service: ShortenApi,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val urlsFlow: Flow<List<ShortURLEntity>>
        get() = urlDao.getShortenedURLHistory()
            .map { it.reversed() }
            .flowOn(defaultDispatcher)
            .conflate()

    /**
     * Fetch a new short URL from the network, and append it to [urlDao]
     */
    suspend fun shortenURL(urlToShorten: String) : Response<ShortURLEntityRoot> {
        Log.d("+++", "urlToShorten")
        val response = service.RETROFIT_SERVICE.getShortenedURL(urlToShorten)
        if (response.isSuccessful && response.body() != null) {
            Log.d("+++", "isSuccessful")
            urlDao.insertNewURLToHistory(response.body()!!.result)
        }
        return response
    }

    suspend fun removeURLFromHistory(urlEntity: ShortURLEntity) {
        urlDao.deleteFromHistory(urlEntity)
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: URLsRepository? = null

        fun getInstance(plantDao: UrlDao, plantService: ShortenApi) =
            instance ?: synchronized(this) {
                instance ?: URLsRepository(plantDao, plantService).also { instance = it }
            }
    }
}

