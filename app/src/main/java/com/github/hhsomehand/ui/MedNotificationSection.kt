package com.github.hhsomehand.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.hhsomehand.ui.component.CornNumberField
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.rememberSharedState
import com.github.hhsomehand.viewmodel.HomeViewModel

@Composable
fun MedNotificationSection() {
    var hourInput by rememberSharedState("MedNotificationSection.hourInput", 4)

    var isNotification by rememberSharedState("MedNotificationSection.isNotification", true)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "吃药后, 隔")

        CornNumberField(
            value = hourInput,
            onValueChange = { hourInput = it },

        )
        Text(text = "个小时, 提醒吃药")

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Switch(
                checked = isNotification,
                onCheckedChange = { isNotification = it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}