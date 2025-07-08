package io.github.madkarmaa.autorestart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import io.github.madkarmaa.autorestart.MainActivity.Companion.app
import io.github.madkarmaa.autorestart.R
import io.github.madkarmaa.autorestart.utils.Logger
import io.github.madkarmaa.autorestart.utils.PreferencesManager
import io.github.madkarmaa.autorestart.utils.SCHEDULE_OPTIONS
import io.github.madkarmaa.autorestart.utils.formatTime
import io.github.madkarmaa.autorestart.utils.getStrRes
import io.github.madkarmaa.autorestart.utils.schedulePeriodicWorkRequest
import io.github.madkarmaa.autorestart.works.RebootDeviceWork


@Composable
fun App(
    schedulePeriodicWorkRequest: (String, PeriodicWorkRequest, ExistingPeriodicWorkPolicy) -> Unit = { key, request, policy ->
        schedulePeriodicWorkRequest(app, key, request, policy)
    }
) {
    // Load saved values from preferences
    var selectedHour by remember { mutableIntStateOf(PreferencesManager.rebootHour) }
    var selectedMinute by remember { mutableIntStateOf(PreferencesManager.rebootMinute) }
    var selectedScheduleOption by remember { mutableStateOf(PreferencesManager.rebootScheduleOption) }

    var showTimePicker by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.getString(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " ${getStrRes(R.string.settings)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                VerticalSpacer(32.dp)

                // Time field row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        getStrRes(R.string.table_field_time),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        formatTime(selectedHour, selectedMinute),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(onClick = { showTimePicker = true }) {
                            Text(getStrRes(R.string.table_update_button))
                        }
                    }
                }

                if (showTimePicker) TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    onConfirm = { hour, minute ->
                        selectedHour = hour
                        selectedMinute = minute
                        showTimePicker = false
                    },
                    initialHour = selectedHour,
                    initialMinute = selectedMinute
                )

                VerticalSpacer(16.dp)

                // Schedule field row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        getStrRes(R.string.table_field_schedule),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        selectedScheduleOption,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(onClick = { dropdownExpanded = true }) {
                            Text(getStrRes(R.string.table_update_button))
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SCHEDULE_OPTIONS.forEach {
                            DropdownMenuItem(text = {
                                Text(
                                    it, style = TextStyle(
                                        textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                                    ), modifier = Modifier.fillMaxWidth()
                                )
                            }, onClick = {
                                selectedScheduleOption = it
                                dropdownExpanded = false
                            })
                        }
                    }
                }

                VerticalSpacer(32.dp)

                // Save button
                Button(
                    onClick = {
                        val request = when (selectedScheduleOption) {
                            "Daily" -> RebootDeviceWork.createPeriodicWorkRequest(
                                selectedHour, selectedMinute
                            )

                            else -> {
                                // Convert day name to day of week (1 = Monday, 7 = Sunday)
                                val dayOfWeek = SCHEDULE_OPTIONS.indexOf(selectedScheduleOption)
                                RebootDeviceWork.createPeriodicWorkRequest(
                                    selectedHour, selectedMinute, dayOfWeek
                                )
                            }
                        }

                        // Save settings to preferences
                        PreferencesManager.saveRebootSchedule(
                            selectedHour, selectedMinute, selectedScheduleOption
                        )

                        schedulePeriodicWorkRequest(
                            RebootDeviceWork.TAG,
                            request,
                            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
                        )
                        Logger.toast(R.string.reboot_scheduled)
                    }, modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        getStrRes(R.string.save_schedule),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}