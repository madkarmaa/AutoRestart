package io.github.madkarmaa.autorestart

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener


class MainActivity : ComponentActivity() {
    private interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    private var currentPermissionCallback: PermissionCallback? = null

    private fun onRequestPermissionsResult(requestCode: Int, grantResult: Int) {
        val granted = grantResult == PackageManager.PERMISSION_GRANTED
        if (granted) {
            Logger.toast(R.string.shizuku_permission_granted)
            currentPermissionCallback?.onPermissionGranted()
        } else {
            Logger.toast(R.string.shizuku_permission_denied)
            currentPermissionCallback?.onPermissionDenied()
        }
    }

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        OnRequestPermissionResultListener { requestCode: Int, grantResult: Int ->
            onRequestPermissionsResult(
                requestCode, grantResult
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://github.com/LSPosed/AndroidHiddenApiBypass/?tab=readme-ov-file#usage
        HiddenApiBypass.addHiddenApiExemptions("")

        enableEdgeToEdge()
        app = this

        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)

        checkPermission(object : PermissionCallback {
            override fun onPermissionGranted() {
                Logger.toast(R.string.shizuku_permission_granted)
            }

            override fun onPermissionDenied() {
                finishAndRemoveTask()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    private fun checkPermission(callback: PermissionCallback? = null, code: Int = 0) {
        currentPermissionCallback = callback

        fun denyPermission(messageResId: Int) {
            Logger.toast(messageResId)
            callback?.onPermissionDenied()
            return
        }

        when {
            Shizuku.isPreV11() -> denyPermission(R.string.shizuku_unsupported)

            !Shizuku.pingBinder() -> denyPermission(R.string.shizuku_not_ready)

            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> {
                callback?.onPermissionGranted()
            }

            Shizuku.shouldShowRequestPermissionRationale() -> denyPermission(R.string.shizuku_permission_denied)

            else -> {
                Logger.toast(R.string.shizuku_requesting_permission)
                Shizuku.requestPermission(code)
            }
        }
    }

    fun schedulePeriodicWorkRequest(
        key: String,
        request: PeriodicWorkRequest,
        action: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
    ) = WorkManager.getInstance(this).enqueueUniquePeriodicWork(key, action, request)

    companion object {
        lateinit var app: MainActivity private set
    }
}