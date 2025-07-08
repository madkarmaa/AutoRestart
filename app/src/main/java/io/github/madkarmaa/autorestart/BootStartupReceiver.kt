package io.github.madkarmaa.autorestart

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BootStartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(
                "AutoRestart.BootStartupReceiver",
                "Received boot completed action, starting background service"
            ) // can't use Logger here
            val serviceIntent = Intent(context, BackgroundService::class.java)
            context.startService(serviceIntent)
        }
    }
}