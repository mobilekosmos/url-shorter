package com.mobilekosmos.android.shortly.data.repository

import android.util.Log
import com.mobilekosmos.android.shortly.data.db.ShortURLEntity
import com.mobilekosmos.android.shortly.data.db.ShortURLEntityRoot
import com.mobilekosmos.android.shortly.data.db.UrlDao
import com.mobilekosmos.android.shortly.data.network.ShortenApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

@ExperimentalCoroutinesApi
class URLsRepository private constructor(
    private val urlDao: UrlDao,
    private val service: ShortenApi,
//    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val shortsFlow: Flow<List<ShortURLEntity>>
        get() = urlDao.getShortenedURLHistory()

    /**
     * Fetch a new short URL from the network, and append it to [urlDao]
     */
    suspend fun urlToShorten(urlToShorten: String) : Response<ShortURLEntityRoot> {
        Log.d("+++", "urlToShorten")
        val response = service.RETROFIT_SERVICE.getShortenedURL(urlToShorten)
        if (response.isSuccessful && response.body() != null) {
            Log.d("+++", "isSuccessful")
            urlDao.insertNewURLToHistory(response.body()!!.result)
//            val res = urlDao.insertNewURLToHistory(response.body()!!.result)
//            if (res.isEmpty()) {
//                Log.d("+++", "res.isEmpty()")
//            } else {
//                Log.d("+++", "res size: ${res.size}")
//            }
        }
        return response
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

