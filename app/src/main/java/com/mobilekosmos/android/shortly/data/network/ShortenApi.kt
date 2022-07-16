package com.mobilekosmos.android.shortly.data.network

import com.mobilekosmos.android.shortly.data.model.ShortURLEntityRoot
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

// With flavors we set this in build.gradle
private const val BASE_URL = "https://api.shrtco.de/v2/"
private const val SHORTEN = "shorten?"

// TODO: encapsulate retrofit results maybe like here: https://proandroiddev.com/modeling-retrofit-responses-with-sealed-classes-and-coroutines-9d6302077dfe
/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 * "As per the Kotlin part of the readme, you must add the KotlinJsonAdapterFactory if you're not using Moshi's codegen. This was a specific behavior change in Moshi 1.9 as per the blog post about Moshi 1.9"
 * https://github.com/square/moshi/blob/master/README.md#kotlin
 */
private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Create the logging interceptor. Search for tag "okhttp" in Logcat.
private val networkLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

// Create the httpClient, configure it
// with cache, network cache interceptor and logging interceptor
// TODO: don't use loggingInterceptor in release build.
private val httpClient = OkHttpClient.Builder()
    .addInterceptor(networkLoggingInterceptor)
    .callTimeout(0, TimeUnit.MINUTES)
    .connectTimeout(0, TimeUnit.MINUTES)
    .readTimeout(0, TimeUnit.MINUTES)
    .build()

// Creates the Retrofit instance with the httpClient.
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient)
    .build()

/**
 * A public interface that exposes the [getShortenedURL] method.
 */
interface ShortenApiService {
    @GET(SHORTEN)
    suspend fun getShortenedURL(@Query(value = "url", encoded = false) url : String): Response<ShortURLEntityRoot>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service.
 */
object ShortenApi {
    val RETROFIT_SERVICE: ShortenApiService by lazy {
        retrofit.create(ShortenApiService::class.java)
    }
}
