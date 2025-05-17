package com.github.hhsomehand.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import com.github.hhsomehand.MainActivity
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.R
import com.github.hhsomehand.constant.PrefsConst
import com.github.hhsomehand.dao.RecordStorage
import com.github.hhsomehand.utils.LogUtils
import com.github.hhsomehand.utils.NotificationUtils
import com.github.hhsomehand.utils.NotificationUtils.CHANNEL_ID
import com.github.hhsomehand.utils.PrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val TAG = "MedicineReminderService"

class MedicineReminderService : Service() {
    private val scope =  CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val recordStorage = RecordStorage()
    private var reminderJob: Job? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L // 每5分钟检查一次

        fun startService(
            isForeground: Boolean,
            context: Context = MyApplication.instance.applicationContext
        ) {
            val intent = Intent(context, MedicineReminderService::class.java)

            if (isForeground) {
                // 前台服务：创建并启动前台通知
                context.startForegroundService(intent)
            } else {
                context.startService(intent) // 非前台服务使用 startService
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化通知通道
        NotificationUtils.initNotificationChannel(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 获取是否运行在前台模式的参数，默认为 true（前台）
        val isForeground = getIsForeground()

        if (isForeground) {
            // 前台服务：创建并启动前台通知
            val notification = createForegroundNotification()
            startForeground(NOTIFICATION_ID, notification)
        } else {
            // 非前台服务：仅记录日志
            LogUtils.d(TAG, "以非前台模式启动服务")
        }

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
            .setPriority(NotificationCompat.PRIORITY_MIN)
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
        withContext(Dispatchers.IO) {
            try {
                val records = recordStorage.getRecordList()

                if (records.isEmpty()) {
                    LogUtils.d(TAG, "没有用药记录")
                    return@withContext
                }

                val latestRecord = records.maxByOrNull { it.date }

                if (latestRecord == null) {
                    LogUtils.d(TAG, "没有用药记录")
                    return@withContext
                }

                val hoursSinceLastDose = ChronoUnit.HOURS.between(latestRecord.date, LocalDateTime.now())

                LogUtils.d(TAG, "检查结果: 上次用药时间: $hoursSinceLastDose 小时前")

                if (hoursSinceLastDose >= getHourInput()) {
                    LogUtils.d(TAG, "需要提醒用药")

                    val timeDiffFmt = recordStorage.getTimeDiffFmt()

                    NotificationUtils.sendSingleNotification(
                        title = "用药提醒",
                        message = "距离上次用药已过去${timeDiffFmt}，请考虑服药！",
                        context = applicationContext
                    )
                } else {
                    // 如果不需要提醒，取消之前的通知
                    NotificationUtils.cancelReminderNotification(applicationContext)
                }
            } catch (e: Exception) {
                LogUtils.e(TAG, "通知过程中出错: ${e.message}")
            }
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

        var isForeground = getIsForeground()

        LogUtils.d(TAG, "isForeground: $isForeground")

        if (isForeground) {
            startForegroundService(restartServiceIntent)
        } else {
            startService(restartServiceIntent)
        }
    }

    fun getIsForeground(): Boolean = PrefsUtils.get(this, PrefsConst.isForegroundKey, PrefsConst.isForegroundValue)

    fun getHourInput(): Int = PrefsUtils.get(this, PrefsConst.hourInputKey, PrefsConst.hourInputDefault)

    override fun onBind(intent: Intent?): IBinder? {
        return null // 不支持绑定
    }
}