/*
 * This file contains code from Hail (https://github.com/aistra0528/Hail)
 * Hail - Freeze Android apps
 * Copyright (C) 2021-2024 Aistra
 * Copyright (C) 2022-2024 Hail contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.madkarmaa.autorestart

import android.content.Context
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import io.github.madkarmaa.autorestart.MainActivity.Companion.app
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper


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

object Device {
    // https://github.com/aistra0528/Hail/blob/master/app/src/main/kotlin/com/aistra/hail/utils/HShizuku.kt#L25-L28
    private fun asInterface(className: String, original: IBinder): Any =
        Class.forName("$className\$Stub").run {
            HiddenApiBypass.invoke(this, null, "asInterface", ShizukuBinderWrapper(original))
        }

    // https://github.com/aistra0528/Hail/blob/master/app/src/main/kotlin/com/aistra/hail/utils/HShizuku.kt#L30-L31
    private fun asInterface(className: String, serviceName: String): Any =
        asInterface(className, SystemServiceHelper.getSystemService(serviceName))

    fun reboot() = runCatching {
        asInterface("android.os.IPowerManager", Context.POWER_SERVICE).let {
            // https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/IPowerManager.aidl#116
            HiddenApiBypass.invoke(it::class.java, it, "reboot", false, null, false)
        }
        true
    }.getOrElse {
        Logger.toast(R.string.device_reboot_failed)
        Logger.error(it)
        false
    }
}