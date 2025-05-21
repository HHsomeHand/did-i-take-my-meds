package com.github.hhsomehand.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import com.github.hhsomehand.MyApplication

object AccessibilityUtils {

    // 跳转到无障碍服务设置页面
    fun openAccessibilitySettings(context: Context = MyApplication.instance.applicationContext) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // 检查无障碍服务是否已启用
    fun isAccessibilityServiceEnabled(context: Context = MyApplication.instance.applicationContext, serviceClass: Class<*>): Boolean {
        val expectedComponentName = ComponentName(context, serviceClass)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (!TextUtils.isEmpty(enabledServices)) {
            val enabledServiceList = enabledServices.split(":")
            return enabledServiceList.any { service ->
                ComponentName.unflattenFromString(service)?.equals(expectedComponentName) == true
            }
        }
        return false
    }
}