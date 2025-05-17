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
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.MedicationReminderWorker
import com.github.hhsomehand.utils.rememberSharedState
import com.github.hhsomehand.viewmodel.HomeViewModel

@Composable
fun MedNotificationSection() {
    // var isNotification by rememberSharedState("MedNotificationSection.isNotification", true)
    var isNotification by rememberSharedState(PrefsConst.isNotificationKey, PrefsConst.isNotificationDefault)

    val context = LocalContext.current
    LaunchedEffect(isNotification) {
        if (isNotification) {
            val intent = Intent(context, MedicineReminderService::class.java)
            startForegroundService(context, intent)
        } else {
            val intent = Intent(context, MedicineReminderService::class.java)
            context.stopService(intent)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "显示吃药间隔的通知")

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