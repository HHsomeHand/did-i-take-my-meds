package com.github.hhsomehand.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.github.hhsomehand.MyApplication

fun openUrl(
    url: String,
    context: Context = MyApplication.instance.applicationContext
) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    } catch (e: Exception) {
        // 处理无效 URL 或无浏览器的情况
        e.printStackTrace()
    }
}