package io.github.madkarmaa.autorestart.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.madkarmaa.autorestart.R
import io.github.madkarmaa.autorestart.utils.getStrRes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean = true
) {
    val state = rememberTimePickerState(
        initialHour = initialHour, initialMinute = initialMinute, is24Hour = is24Hour
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(getStrRes(R.string.time_picker_title)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                TimePicker(
                    state = state
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(state.hour, state.minute)
                }) {
                Text(getStrRes(R.string.confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(getStrRes(R.string.cancel))
            }
        })
}