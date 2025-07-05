package io.github.madkarmaa.autorestart

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
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
            toast(R.string.shizuku_permission_granted)
            currentPermissionCallback?.onPermissionGranted()
        } else {
            toast(R.string.shizuku_permission_denied, Log.ERROR)
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
        enableEdgeToEdge()
        app = this

        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)

        checkPermission(object : PermissionCallback {
            override fun onPermissionGranted() {
                // Do whatever needs to happen after permission is granted
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

        if (Shizuku.isPreV11()) {
            toast(R.string.shizuku_unsupported, Log.ERROR)
            callback?.onPermissionDenied()
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            callback?.onPermissionGranted()
            return
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            toast(R.string.shizuku_permission_denied, Log.ERROR)
            callback?.onPermissionDenied()
            return
        } else {
            toast(R.string.shizuku_requesting_permission)
            Shizuku.requestPermission(code)
        }
    }

    companion object {
        lateinit var app: MainActivity private set
    }
}