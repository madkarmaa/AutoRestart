package io.github.madkarmaa.autorestart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import io.github.madkarmaa.autorestart.Logger
import io.github.madkarmaa.autorestart.MainActivity.Companion.app
import io.github.madkarmaa.autorestart.R
import io.github.madkarmaa.autorestart.SCHEDULE_OPTIONS
import io.github.madkarmaa.autorestart.formatTime
import io.github.madkarmaa.autorestart.schedulePeriodicWorkRequest
import io.github.madkarmaa.autorestart.works.RebootDeviceWork


@Composable
fun AutoRestartApp(
    schedulePeriodicWorkRequest: (String, PeriodicWorkRequest, ExistingPeriodicWorkPolicy) -> Unit = { key, request, policy ->
        schedulePeriodicWorkRequest(app, key, request, policy)
    }
) {
    var selectedHour by remember { mutableIntStateOf(3) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedScheduleOption by remember { mutableStateOf(SCHEDULE_OPTIONS[0]) }

    MaterialTheme {
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
                Text(
                    text = "Auto Restart Settings", style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Time picker button
                Button(onClick = { showTimePicker = true }) {
                    Text("Select Time: ${formatTime(selectedHour, selectedMinute)}")
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


                Spacer(modifier = Modifier.height(16.dp))

                // Schedule type dropdown
                Box {
                    Button(onClick = { expanded = true }) {
                        Text("Schedule: $selectedScheduleOption")
                    }

                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        SCHEDULE_OPTIONS.forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = {
                                selectedScheduleOption = it
                                expanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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

                        schedulePeriodicWorkRequest(
                            RebootDeviceWork.TAG,
                            request,
                            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
                        )
                        Logger.toast(R.string.reboot_scheduled)
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Schedule")
                }
            }
        }
    }
}
