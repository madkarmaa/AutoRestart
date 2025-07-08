package io.github.madkarmaa.autorestart

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.github.madkarmaa.autorestart.MainActivity.Companion.app
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.reflect.KProperty


fun schedulePeriodicWorkRequest(
    ctx: Context,
    key: String,
    request: PeriodicWorkRequest,
    action: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
) = WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(key, action, request)

fun formatTime(hour: Int, minute: Int) =
    String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

fun getStrRes(resId: Int) = app.getString(resId)

val SCHEDULE_OPTIONS = listOf(getStrRes(R.string.option_daily)) + DayOfWeek.entries.map {
    it.getDisplayName(TextStyle.FULL, Locale.getDefault())
}

class LoggableProperty<T>(private var value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        Logger.debug("Getting ${property.name}: $value")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Logger.debug("Setting ${property.name}: $value")
        this.value = value
    }
}