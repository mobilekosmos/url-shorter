package com.mobilekosmos.android.shortly

import android.os.StrictMode
import timber.log.Timber.*
import timber.log.Timber.Forest.plant

class MyDebugApplication : MyApplication() {

    override fun onCreate() {
        super.onCreate()
        StrictMode.enableDefaults()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
//            plant(CrashReportingTree())
        }
    }
}