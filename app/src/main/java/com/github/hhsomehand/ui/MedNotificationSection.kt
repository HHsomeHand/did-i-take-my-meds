package com.github.hhsomehand.ui

import android.content.Intent
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
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.constant.PrefsConst
import com.github.hhsomehand.service.MedicineReminderService
import com.github.hhsomehand.ui.component.CornNumberField
import com.github.hhsomehand.ui.theme.ConfigRowHeight
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.MedicationReminderWorker
import com.github.hhsomehand.utils.rememberSharedState
import com.github.hhsomehand.viewmodel.HomeViewModel
import com.judemanutd.autostarter.AutoStartPermissionHelper

@Composable
fun MedNotificationSection() {
    // var isNotification by rememberSharedState("MedNotificationSection.isNotification", true)
    var isNotification by rememberSharedState(PrefsConst.isNotificationKey, PrefsConst.isNotificationDefault)

    var isForeground by rememberSharedState(PrefsConst.isForegroundKey, PrefsConst.isForegroundValue)

    var hourInput by rememberSharedState(PrefsConst.hourInputKey, PrefsConst.hourInputDefault)

    var minToCheck by rememberSharedState(PrefsConst.minToCheckKey, PrefsConst.minToCheckValue)

    val context = LocalContext.current
    LaunchedEffect(isNotification) {
        if (isNotification) {

            MedicineReminderService.startService(isForeground)

        } else {

            MedicineReminderService.stopService()

        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "通过发消息通知来提醒吃药")

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "是否启动前台服务来发通知")

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Switch(
                checked = isForeground,
                onCheckedChange = { isForeground = it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "吃药后, 隔")

        CornNumberField(
            value = hourInput,
            onValueChange = { hourInput = it },
            )

        Text(text = "个小时, 提醒吃药")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "每隔")

        CornNumberField(
            value = minToCheck,
            onValueChange = { minToCheck = it },
            )

        Text(text = "分钟, 检查是否超时")
    }

    CornOutlinedButton(
        onClick = {
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(context)
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("打开自启动设置")
    }
}