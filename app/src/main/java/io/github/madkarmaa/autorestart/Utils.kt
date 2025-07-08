package io.github.madkarmaa.autorestart

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale


fun schedulePeriodicWorkRequest(
    ctx: Context,
    key: String,
    request: PeriodicWorkRequest,
    action: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
) = WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(key, action, request)

fun formatTime(hour: Int, minute: Int) = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute)

val SCHEDULE_OPTIONS = listOf("Daily") + DayOfWeek.entries.map {
    it.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
}