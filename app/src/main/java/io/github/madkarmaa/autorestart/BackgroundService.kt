package io.github.madkarmaa.autorestart

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class BackgroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(
            "AutoRestart.BackgroundService",
            "App started in the background"
        ) // can't use Logger here
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}