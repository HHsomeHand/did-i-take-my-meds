package com.github.hhsomehand.utils

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.provider.AlarmClock
import android.util.Log
import com.github.hhsomehand.MainActivity
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.utils.extension.showToast

/**
 * AndroidManifest.xml:
 *
 *  <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
 */
object AlarmUtils {
    fun setAlarm(
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        message: String = "",
        context: Context
    ) {
        // 使用系统闹钟应用的倒计时
        val totalSeconds = hour * 3600 + minute * 60 + second

        if (totalSeconds > 0) {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, totalSeconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false) // 显示系统闹钟 UI
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}