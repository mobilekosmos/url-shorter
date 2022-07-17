package com.mobilekosmos.android.shortly

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.mobilekosmos.android.shortly.data.db.AppDatabase
import com.mobilekosmos.android.shortly.data.network.ShortenApi
import com.mobilekosmos.android.shortly.data.repository.URLsRepository
import com.mobilekosmos.android.shortly.ui.MainFragment

interface ViewModelFactoryProvider {
    fun provideMyViewModelFactory(context: Context): MainFragment.MyViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {
    private fun getUrlRepository(context: Context): URLsRepository {
        return URLsRepository.getInstance(
            urlDao(context),
            urlService()
        )
    }

    private fun urlService() = ShortenApi

    private fun urlDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).urlDao()

    override fun provideMyViewModelFactory(context: Context): MainFragment.MyViewModelFactory {
        val repository = getUrlRepository(context)
        return MainFragment.MyViewModelFactory(repository)
    }
}

private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider


@VisibleForTesting
private fun setInjectorForTesting(injector: ViewModelFactoryProvider?) {
    synchronized(Lock) {
        currentInjector = injector ?: DefaultViewModelProvider
    }
}

@VisibleForTesting
private fun resetInjector() =
    setInjectorForTesting(null)