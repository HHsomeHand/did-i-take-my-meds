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
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.myapp.utils.NotificationUtils
import com.github.hhsomehand.ui.RecordSection

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
                Surface(
                    Modifier
                        .fillMaxSize()
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(horizontal = 10.dp)
        ) {
            RecordSection()
        }
    }
}

@Composable
fun Test(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var text by rememberSaveable { mutableStateOf("") }
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

        Spacer(Modifier.height(10.dp))

        TextField(
            value = text,
            onValueChange = {
                text = it
            }
        )
    }
}