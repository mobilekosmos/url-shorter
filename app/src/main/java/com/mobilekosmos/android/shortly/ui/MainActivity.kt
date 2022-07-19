package com.mobilekosmos.android.shortly.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.mobilekosmos.android.shortly.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logging twice just for testing purposes, this is a demo app!
        Timber.d("onCreate")
        Log.d("MainActivity", "onCreate")

        // Applies edge-to-edge screen mode.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)
    }
}
