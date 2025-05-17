package com.github.hhsomehand.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.hhsomehand.MainActivity
import com.github.hhsomehand.R
import com.github.hhsomehand.dao.RecordStorage
import com.github.hhsomehand.utils.LogUtils
import com.github.hhsomehand.utils.NotificationUtils
import com.github.hhsomehand.utils.NotificationUtils.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val TAG = "MedicineReminderService"

class MedicineReminderService : Service() {
    private val scope =  CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val recordStorage = RecordStorage()
    private var reminderJob: Job? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val REMINDER_INTERVAL_MINUTES = 4 * 60 // 4小时
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L // 每5分钟检查一次
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化通知通道
        NotificationUtils.initNotificationChannel(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 创建前台通知
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)

        // 启动定时检查任务
        startReminderCheck()

        return START_STICKY // 服务被杀死后尝试重启
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("用药提醒服务")
            .setContentText("正在监控您的用药时间...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 持久通知
            .build()
    }

    private fun startReminderCheck() {
        reminderJob?.cancel() // 避免重复启动
        reminderJob = scope.launch {
            while (true) {
                LogUtils.d(TAG, "开始检查")

                checkAndNotify()

                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private suspend fun checkAndNotify() {
        val records = recordStorage.getRecordList()

        if (records.isEmpty()) {
            return
        }

        val latestRecord = records.maxByOrNull { it.date } ?: return

        val minutesSinceLastDose = ChronoUnit.MINUTES.between(latestRecord.date, LocalDateTime.now())
        val timeDiffFmt = recordStorage.getTimeDiffFmt()

        if (minutesSinceLastDose >= REMINDER_INTERVAL_MINUTES) {
            NotificationUtils.sendNotification(
                title = "用药提醒",
                message = "距离上次用药已过去${timeDiffFmt}，请考虑服药！",
                context = applicationContext
            )
        }
    }

    override fun onDestroy() {
        LogUtils.d(TAG, "停止服务")
        super.onDestroy()
        scope.cancel() // 取消协程
        stopForeground(STOP_FOREGROUND_REMOVE) // 移除前台通知
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        LogUtils.d(TAG, "服务被移除，尝试重启")
        val restartServiceIntent = Intent(applicationContext, this::class.java)
        restartServiceIntent.setPackage(packageName)
        startForegroundService(restartServiceIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // 不支持绑定
    }
}