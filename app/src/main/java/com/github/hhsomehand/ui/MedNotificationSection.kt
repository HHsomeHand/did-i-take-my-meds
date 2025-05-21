package com.github.hhsomehand.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.constant.LocalStorageConst.isHideApp
import com.github.hhsomehand.constant.PrefsConst
import com.github.hhsomehand.service.MedicineReminderService
import com.github.hhsomehand.ui.component.CornNumberField
import com.github.hhsomehand.ui.dialog.CornDialog
import com.github.hhsomehand.ui.dialog.getDialogBoxModifier
import com.github.hhsomehand.ui.dialog.getDialogModifier
import com.github.hhsomehand.ui.theme.ConfigRowHeight
import com.github.hhsomehand.ui.theme.Spacing
import com.github.hhsomehand.utils.AccessibilityUtils
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.MedicationReminderWorker
import com.github.hhsomehand.utils.hideAppWindow
import com.github.hhsomehand.utils.openUrl
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
        Text(text = "本 App 会发消息提醒您吃药")

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

    var isShowDialog by rememberSaveable { mutableStateOf(false) }

    CornOutlinedButton(
        onClick = {
            isShowDialog = true
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("高级设置")
    }

    CornDialog(
        isShowDialog = isShowDialog,
        onDismiss = {
            isShowDialog = false
        }
    ) {
        Column(
            modifier = getDialogModifier(
                Modifier
                    .fillMaxWidth()
            )

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = ConfigRowHeight
            ) {
                Text(text = "是否启动前台服务")

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

            HideAppSection()

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

                Text(text = "分钟, 检查是否要提醒")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = ConfigRowHeight
            ) {
                SelectionContainer {
                    Text("加 QQ 群获取版本更新: 1038206078")
                }

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


            CornOutlinedButton(
                onClick = {
                    openUrl("https://gitee.com/HHandHsome/did-i-take-my-meds")
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("中文说明书 & 开源地址")
            }

            CornOutlinedButton(
                onClick = {
                    openUrl("https://github.com/HHsomeHand/did-i-take-my-meds")
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("英文说明书 & 开源地址")
            }

            AccessibilitySection()
        }
    }
}

@Composable
fun HideAppSection() {
    val viewModel: HomeViewModel = viewModel()
    val isHideApp by viewModel.isHideApp.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = ConfigRowHeight
    ) {
        Text(text = "在任务栏中, 隐藏 App 窗口")

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Switch(
                checked = isHideApp,
                onCheckedChange = { viewModel.updateIsHideApp(it) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun AccessibilitySection() {
    CornOutlinedButton(
        onClick = { AccessibilityUtils.openAccessibilitySettings() },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("打开无障碍权限设置")
    }
}