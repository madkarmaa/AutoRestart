package io.github.madkarmaa.autorestart

import android.util.Log
import android.widget.Toast
import io.github.madkarmaa.autorestart.MainActivity.Companion.app


fun toast(message: String, level: Int = Log.INFO) {
    Log.println(level, app.getString(R.string.app_name), message)
    Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
}

fun toast(resId: Int, level: Int = Log.INFO) = toast(app.getString(resId), level)