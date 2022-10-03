package com.commoncents.utils

import android.util.Log
import com.commoncents.BuildConfig

object Logger {

    const val TAG: String = "Common cents"

    fun e(message: String, s: String) {
        if (isLoggable) {
            try {
                Log.e(TAG, "$message-->$s")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun i(message: String) {
        if (isLoggable) {
            try {
                Log.i(TAG, message + "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun d(message: String) {
        if (isLoggable) {
            try {
                Log.d(TAG, message + "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun w(message: String) {
        if (isLoggable) {
            try {
                Log.w(TAG, message + "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun v(message: String) {
        if (isLoggable) {
            try {
                Log.v(TAG, message + "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val isLoggable: Boolean
        get() = BuildConfig.DEBUG
}