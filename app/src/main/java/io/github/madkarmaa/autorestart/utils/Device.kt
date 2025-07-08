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

package io.github.madkarmaa.autorestart.utils

import android.content.Context
import android.os.IBinder
import io.github.madkarmaa.autorestart.R
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper


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
        Logger.info("Rebooting device, bye!")
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