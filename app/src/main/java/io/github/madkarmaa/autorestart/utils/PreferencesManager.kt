package io.github.madkarmaa.autorestart.utils

import android.content.Context
import android.content.SharedPreferences
import io.github.madkarmaa.autorestart.MainActivity.Companion.app


object PreferencesManager {
    private const val PREFERENCES_NAME = "autorestart_preferences"
    private const val KEY_HOUR = "reboot_hour"
    private const val KEY_MINUTE = "reboot_minute"
    private const val KEY_SCHEDULE_OPTION = "schedule_option"

    private const val DEFAULT_HOUR = 3
    private const val DEFAULT_MINUTE = 0
    private val DEFAULT_SCHEDULE_OPTION = SCHEDULE_OPTIONS[0]

    private val sharedPreferences: SharedPreferences by lazy {
        app.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveRebootSchedule(hour: Int, minute: Int, scheduleOption: String) {
        sharedPreferences.edit().apply {
            putInt(KEY_HOUR, hour)
            putInt(KEY_MINUTE, minute)
            putString(KEY_SCHEDULE_OPTION, scheduleOption)
            apply()
        }
    }

    val rebootHour: Int by LoggableProperty(sharedPreferences.getInt(KEY_HOUR, DEFAULT_HOUR))

    val rebootMinute: Int by LoggableProperty(sharedPreferences.getInt(KEY_MINUTE, DEFAULT_MINUTE))

    val rebootScheduleOption: String by LoggableProperty(
        sharedPreferences.getString(KEY_SCHEDULE_OPTION, DEFAULT_SCHEDULE_OPTION)
            ?: DEFAULT_SCHEDULE_OPTION
    )
}