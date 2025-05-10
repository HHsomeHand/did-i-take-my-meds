package com.github.hhsomehand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiditakemymedsTheme {
        Greeting("Android")
    }
}