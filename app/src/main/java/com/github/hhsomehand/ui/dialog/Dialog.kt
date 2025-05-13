package com.github.hhsomehand.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.hhsomehand.ui.theme.MyWhite
import java.time.LocalDateTime

@Composable
fun getDialogBoxModifier(): Modifier {
    return Modifier
        .clip(MaterialTheme.shapes.large)
        .background(MaterialTheme.colorScheme.surface)
        .padding(15.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    isShowDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState,
) {
    AnimatedVisibility(isShowDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismiss
        ) {
            Box(
                modifier = getDialogBoxModifier()
            ) {
                Column {
                    TimePicker(
                        state = timePickerState,
                    )

                    Button(
                        onClick = onDismiss
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = onConfirm
                    ) {
                        Text("确定修改")
                    }
                }
            }
        }
    }
}