package com.github.hhsomehand

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.github.hhsomehand.ui.theme.DiditakemymedsTheme
import com.github.hhsomehand.utils.AlarmUtils
import com.github.hhsomehand.utils.extension.showToast
import com.permissionx.guolindev.PermissionX
import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.example.myapp.utils.NotificationUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionX.init(this)
            .permissions(Manifest.permission.POST_NOTIFICATIONS)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "吃药 App 需要权限来通知您服药", "我已明白", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    NotificationUtils.initNotificationChannel()
                } else {
                    "请给应用赋予通知权限, 可重装应用, 重新授权".showToast()
                }
            }

        // enableEdgeToEdge()
        setContent {
            DiditakemymedsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column {
        Button(
            onClick = {
                AlarmUtils.setAlarm(
                    second = 5,
                    context = context,
                    message = "刷牙"
                )
                "开始闹钟!".showToast()
            }
        ) {
            Text("Begin Alarm!")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                NotificationUtils.sendNotification(
                    title = "title",
                    message = "该吃药了"
                )
            }
        ) {
            Text("发送消息提示")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiditakemymedsTheme {
        Greeting("Android")
    }
}