package com.github.hhsomehand.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.hhsomehand.MainActivity
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.R
import com.github.hhsomehand.utils.LogUtils
import java.util.UUID

object NotificationUtils {
    private const val logTag = "sendNotification"
    var isInit = false
        private set
    const val CHANNEL_ID = "default_channel"
    const val CHANNEL_NAME = "Default Notifications"
    const val CHANNEL_DESCRIPTION = "General notifications for the app"

    // 初始化通知通道（仅需在应用启动时调用一次）
    fun initNotificationChannel(
        context: Context = MyApplication.instance.applicationContext
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)

        isInit = true
    }

    // 封装的通知发送函数
    // 请用 PermissionX 进行手动申请权限
    @SuppressLint("MissingPermission")
    fun sendNotification(
        title: String,
        message: String,
        targetActivity: Class<*> = MainActivity::class.java,
        context: Context = MyApplication.instance.applicationContext,
    ) {
        if (!isInit) {
            LogUtils.e(logTag, "使用 sendNotification 前, 请先调用 initNotificationChannel 进行初始化")

            return
        }

        val intent = Intent(context, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = UUID.randomUUID().hashCode() // 随机生成通知ID
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // 替换为您的图标
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // 设置点击行为
            .setAutoCancel(true) // 点击后自动消失

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    // 用于用药提醒的固定通知ID
    private const val REMINDER_NOTIFICATION_ID = 1002

    // 发送单例通知（替换之前的提醒）
    @SuppressLint("MissingPermission")
    fun sendSingleNotification(
        title: String,
        message: String,
        targetActivity: Class<*> = MainActivity::class.java,
        context: Context = MyApplication.instance.applicationContext,
    ) {
        if (!isInit) {
            LogUtils.e(logTag, "使用 sendNotification 前, 请先调用 initNotificationChannel 进行初始化")
            return
        }

        // 先取消之前的通知
        cancelReminderNotification(context)

        val intent = Intent(context, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true) // 只在第一次显示时提醒（震动、声音）

        with(NotificationManagerCompat.from(context)) {
            notify(REMINDER_NOTIFICATION_ID, builder.build())
        }
    }

    // 取消用药提醒通知
    fun cancelReminderNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(REMINDER_NOTIFICATION_ID)
    }
}