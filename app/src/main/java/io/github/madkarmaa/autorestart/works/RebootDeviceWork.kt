package io.github.madkarmaa.autorestart.works

import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.madkarmaa.autorestart.Device
import io.github.madkarmaa.autorestart.MainActivity.Companion.app
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit


class RebootDeviceWork(params: WorkerParameters) : Worker(app, params) {
    override fun doWork(): Result {
        return if (Device.reboot()) Result.success()
        else Result.failure()
    }

    companion object {
        const val TAG = "reboot_device_work"

        /**
         * Creates a work request to reboot the device at a specific time **daily**.
         *
         * @param hour The hour of the day (0-23) to perform the reboot
         * @param minute The minute of the hour (0-59) to perform the reboot
         * @param constraints Optional constraints that must be satisfied for the work to run
         * @return WorkRequest configured for **daily** execution at the specified time
         */
        fun createPeriodicWorkRequest(
            hour: Int, minute: Int, constraints: Constraints = Constraints.NONE
        ): PeriodicWorkRequest = createPeriodicWorkRequest(hour, minute, true, 1, constraints)

        /**
         * Creates a work request to reboot the device at a specific time **on a specific day of the week**.
         *
         * @param hour The hour of the day (0-23) to perform the reboot
         * @param minute The minute of the hour (0-59) to perform the reboot
         * @param dayOfWeek Day of week to run on (1 = Monday, 7 = Sunday)
         * @param constraints Optional constraints that must be satisfied for the work to run
         * @return WorkRequest configured for **weekly execution** on the specified day and time
         */
        fun createPeriodicWorkRequest(
            hour: Int, minute: Int, dayOfWeek: Int, constraints: Constraints = Constraints.NONE
        ): PeriodicWorkRequest =
            createPeriodicWorkRequest(hour, minute, false, dayOfWeek, constraints)

        /**
         * Base implementation for creating a work request with full scheduling options.
         *
         * @param hour The hour of the day (0-23) to perform the reboot
         * @param minute The minute of the hour (0-59) to perform the reboot
         * @param isDailySchedule If true, runs daily. If false, uses the dayOfWeek parameter
         * @param dayOfWeek Day of week to run on (1 = Monday, 7 = Sunday), only used if isDailySchedule is false
         * @param constraints Constraints that must be satisfied for the work to run
         * @return WorkRequest configured with the specified schedule
         */
        private fun createPeriodicWorkRequest(
            hour: Int,
            minute: Int,
            isDailySchedule: Boolean,
            dayOfWeek: Int,
            constraints: Constraints = Constraints.NONE
        ): PeriodicWorkRequest {
            // Calculate delay until the next occurrence of the specified time
            val now = LocalDateTime.now()
            val targetTime = LocalTime.of(hour, minute)
            var targetDateTime = now.with(targetTime)

            // If the time has already passed today, schedule for the next applicable day
            if (targetDateTime.isBefore(now)) {
                if (isDailySchedule)
                // Schedule for tomorrow
                    targetDateTime = targetDateTime.plusDays(1)
                else {
                    // Find the next occurrence of the specified day of week
                    val today = now.dayOfWeek.value

                    if (today == dayOfWeek)
                    // If it's the target day but time has passed, schedule for next week
                        targetDateTime = targetDateTime.plusDays(7)
                    else {
                        // Calculate days to add to reach the target day
                        var daysToAdd = dayOfWeek - today
                        if (daysToAdd <= 0) daysToAdd += 7  // Wrap around to next week

                        targetDateTime = LocalDateTime.of(
                            now.toLocalDate().plusDays(daysToAdd.toLong()), targetTime
                        )
                    }
                }
            } else if (!isDailySchedule) {
                // Check if today is the specified day
                val today = now.dayOfWeek.value

                if (today != dayOfWeek) {
                    // Calculate days to add to reach the target day
                    var daysToAdd = dayOfWeek - today
                    if (daysToAdd <= 0) daysToAdd += 7  // Wrap around to next week

                    targetDateTime =
                        LocalDateTime.of(now.toLocalDate().plusDays(daysToAdd.toLong()), targetTime)
                }
            }

            // Calculate the initial delay
            val initialDelayMillis = Duration.between(now, targetDateTime).toMillis()
            // Determine the repeat interval (daily or weekly)
            val repeatInterval = if (isDailySchedule) 24 else 24 * 7

            return PeriodicWorkRequestBuilder<RebootDeviceWork>(
                repeatInterval.toLong(), TimeUnit.HOURS
            ).setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS).setConstraints(constraints)
                .addTag(TAG).build()
        }
    }
}