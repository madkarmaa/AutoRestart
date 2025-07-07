package io.github.madkarmaa.autorestart

import android.util.Log
import android.widget.Toast
import io.github.madkarmaa.autorestart.MainActivity.Companion.app


object Logger {
    private val TAG = app.getString(R.string.app_name)

    fun error(throwable: Throwable) {
        Log.e(TAG, throwable.message, throwable)
    }

    fun error(message: String) {
        Log.e(TAG, message)
    }

    fun info(message: String) {
        Log.i(TAG, message)
    }

    fun debug(message: String) {
        Log.d(TAG, message)
    }

    fun warn(message: String) {
        Log.w(TAG, message)
    }

    fun toast(message: String) {
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
    }

    fun toast(resId: Int) = toast(app.getString(resId))
}