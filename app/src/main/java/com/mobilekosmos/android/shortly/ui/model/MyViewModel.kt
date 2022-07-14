package com.mobilekosmos.android.shortly.ui.model

import android.util.Log
import androidx.lifecycle.*
import com.mobilekosmos.android.shortly.data.db.ShortURLEntity
import com.mobilekosmos.android.shortly.data.repository.URLsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

//class ClubsViewModelFlow : ViewModel() {
class MyViewModel @OptIn(ExperimentalCoroutinesApi::class)
internal constructor(
    private val urLsRepository: URLsRepository
) : ViewModel() {

//    // Uses prefix "_" as it's the naming convention used in backing properties.
//    // We use LazyThreadSafetyMode.NONE to avoid using thread synchronization because we are using this only from the MainThread.
//    private val _shorts: MutableLiveData<List<ShortEntity>> by lazy(LazyThreadSafetyMode.NONE) {
//        // by lazy normally expects a value but since we load async we create a mutable liveData with no value assigned to it.
//        // "also" returns the empty MutableLiveData and at the same time runs a lambda.
//        // The real value is then set in the called function.
//        MutableLiveData<List<ShortEntity>>().also {
//            fetchShorterURLFromRepository()
//        }
//    }
//
//    // LiveData.value is still @Nullable so you must always use ?operator when accessing.
//    // https://proandroiddev.com/improving-livedata-nullability-in-kotlin-45751a2bafb7
//    val shorts: LiveData<List<ShortEntity>>
//        get() = _shorts

    /**
     * A list of plants that updates based on the current filter.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val shorts: LiveData<List<ShortURLEntity>> = urLsRepository.shortsFlow.asLiveData()

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    // We use LazyThreadSafetyMode.NONE to avoid using thread synchronization because we are using this only from the MainThread.
    private val _eventNetworkError: MutableLiveData<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        MutableLiveData<Boolean>()
    }

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * Fetches data using Retrofit which is configured to cache the response for a short period of time.
     * (Good solution for simple requests and responses, infrequent network calls, or small datasets.)
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchShorterURLFromRepository(urlToShorten: String) {
        _isNetworkErrorShown.value = false
        _eventNetworkError.value = false
        viewModelScope.launch {
            try {
                // Here you can not call _clubs.value = clubsRepository.getAllClubs()
                // because the lazy initialization did not finish yet and you would actually recall this function again.

                // To avoid saving the resulting array in a variable we work with the retrofit response instead.
                // TODO: Ideally we shouldn't know anything about Retrofit here, but just for simplicity we do. There is a todo in the retrofit api class to address this.
                val apiResponse = urLsRepository.urlToShorten("https://www.google.com")
                val shortenedURL = apiResponse.body()
                if (apiResponse.isSuccessful && shortenedURL != null) {
                    // Do nothing.
                    Log.d("+++", "url: $shortenedURL")
                } else {
                    // TODO: handle error
                }
                // viewModelScope uses the MainThread Dispatcher by default so we don't need to use "withContext(Dispatchers.Main)"
                // to access the UI.
            } catch (ex: Exception) {
                // TODO: extend/improve error handling: check internet connection, listen to connection state changes and automatically retry, etc.
//                _eventNetworkError.value = true
            }
        }
    }

    fun retryLoadingData() {
//        fetchShorterURLFromRepository()
    }

    fun fetchShorterURL(urlToShorten: String) {
        fetchShorterURLFromRepository(urlToShorten)
    }
}