package com.github.hhsomehand.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.constant.PrefsConst
import com.github.hhsomehand.dao.RecordStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MedicationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PrefsConst.appStoreName, Context.MODE_PRIVATE)
    private val recordStorage = RecordStorage(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 检查是否启用通知
            // val isNotificationEnabled = sharedPreferences.getBoolean("MedNotificationSection.isNotification", true)
            val isNotificationEnabled = sharedPreferences.getBoolean(PrefsConst.isNotificationKey, PrefsConst.isNotificationDefault)
            if (!isNotificationEnabled) {
                android.util.Log.d("MedicationReminderWorker", "没开允许通知")

                return@withContext Result.success()
            }

            // 获取提醒间隔小时数
            // val hourInterval = sharedPreferences.getInt("MedNotificationSection.hourInput", 4)
            val hourInterval = sharedPreferences.getInt(PrefsConst.hourInputKey, PrefsConst.hourInputDefault) // 默认4小时
            if (hourInterval <= 0) {
                android.util.Log.d("MedicationReminderWorker", "没到需要提醒的时间")

                return@withContext Result.success()
            }

            // 获取最新的服药记录
            val records = recordStorage.getRecordList()
            val latestRecord = records.maxByOrNull { it.date }

            if (latestRecord != null) {
                val currentTime = LocalDateTime.now()
                val hoursSinceLastDose = ChronoUnit.HOURS.between(latestRecord.date, currentTime)

                // 检查是否超过间隔时间
                if (hoursSinceLastDose >= hourInterval) {
                    // 发送通知
                    NotificationUtils.sendNotification(
                        title = "吃药提醒",
                        message = "距离上次服药已过去${hoursSinceLastDose}小时，请按时服药！",
                        context = applicationContext
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            // 记录错误并重试
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "MedicationReminderWork"

        // 启动 WorkManager 的方法
        fun scheduleWork(context: Context = MyApplication.instance.applicationContext) {
            val workManager = androidx.work.WorkManager.getInstance(context)

            // 创建周期性工作请求，每小时检查一次
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<MedicationReminderWorker>(
                repeatInterval = 1, // 每1小时
                repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.HOURS
            )
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            // 使用唯一工作名称，确保不会重复调度
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        // 取消 WorkManager 的方法
        fun cancelWork(context: Context = MyApplication.instance.applicationContext) {
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        // 测试 WorkManager 内部逻辑是否正常, 10 秒后运行
        fun testRunOnce(context: Context = MyApplication.instance.applicationContext) {
            val workManager = androidx.work.WorkManager.getInstance(context)

            val testRequest = androidx.work.OneTimeWorkRequestBuilder<MedicationReminderWorker>()
                .setInitialDelay(2, java.util.concurrent.TimeUnit.SECONDS) // 延迟10秒执行
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            workManager.enqueue(testRequest)

            android.util.Log.d("MedicationReminderWorker", "Test OneTimeWorkRequest enqueued (delay 10s)")
        }

    }
}