package io.github.madkarmaa.autorestart

import android.content.Context
import android.os.IBinder
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