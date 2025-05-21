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
import com.github.hhsomehand.ui.theme.ConfigRowHeight
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.rememberSharedState
import com.github.hhsomehand.viewmodel.HomeViewModel

@Composable
fun BrushAlarmSection() {
    var minInput by rememberSharedState("BrushAlarmSection.gapInput", 30)

    var isAlarm by rememberSharedState("BrushAlarmSection.isAlarm", true)

    val viewModel: HomeViewModel = viewModel()

    var context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.recordListAdd.collect {
            if (isAlarm) {
                AlarmUtils.setAlarm(
                    minute = minInput,
                    message = "刷牙",
                    context = context
                )
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "吃药后, 开启")

        CornNumberField(
            value = minInput,
            onValueChange = { minInput = it },

        )
        Text(text = "分钟的刷牙倒计时")

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Switch(
                checked = isAlarm,
                onCheckedChange = { isAlarm = it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}