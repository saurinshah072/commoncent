package com.commoncents.core

import androidx.appcompat.app.AppCompatActivity
import com.commoncents.network.NetworkHelper
import com.commoncents.preferences.Preferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var networkHelper: NetworkHelper
}