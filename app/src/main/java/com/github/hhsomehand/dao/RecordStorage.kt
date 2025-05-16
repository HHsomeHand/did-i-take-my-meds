package com.github.hhsomehand.dao

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.model.MedRecord
import com.github.hhsomehand.utils.LogUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "RecordStorage"
private const val RECORD_KEY = "MEDICAL_RECORDS"

class RecordStorage(context: Context = MyApplication.instance.applicationContext) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MedicalRecords", Context.MODE_PRIVATE)
    private val gson: Gson

    init {
        // 注册 LocalDateTime 的 Gson 适配器, 不然 Gson 没办法正常序列化 LocalDateTime, 实在是太弱智了
        val builder = GsonBuilder()
        builder.registerTypeAdapter(LocalDateTime::class.java, object : TypeAdapter<LocalDateTime>() {
            private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

            override fun write(out: JsonWriter, value: LocalDateTime?) {
                out.value(value?.format(formatter))
            }

            override fun read(`in`: JsonReader): LocalDateTime {
                val string = `in`.nextString()
                return LocalDateTime.parse(string, formatter)
            }
        })
        gson = builder.create()
    }

    suspend fun storeRecordList(newList: List<MedRecord>) {
        withContext(Dispatchers.IO) {
            val json = gson.toJson(newList)
            sharedPreferences.edit()
                .putString(RECORD_KEY, json)
                .apply()
        }
    }

    suspend fun getRecordList(): List<MedRecord> {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(RECORD_KEY, null)

            if (json != null) {
                try {
                    val type = object : TypeToken<List<MedRecord>>() {}.type
                    gson.fromJson(json, type) ?: emptyList()
                } catch (e: Exception) {
                    LogUtils.e(TAG, "JSON 解析失败: $e")
                    // 清理无效数据
                    sharedPreferences.edit().remove(RECORD_KEY).apply()
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    // 更新时间间隔的字符串
    suspend fun getTimeDiffFmt(): String {
        val newestRecord = getRecordList().maxByOrNull { it.date }

        var resultFmt: String = ""

        if (newestRecord == null) {
            return resultFmt
        }

        val duration = Duration.between(newestRecord.date, LocalDateTime.now())

        val hourFmt = if (duration.toHours() != 0L) {
            String.format("%d小时", duration.toHours())
        } else {
            ""
        }

        var durationMin = duration.toMinutes() - duration.toHours() * 60

        if (durationMin < 0) {
            durationMin = 0 // 不会到这里
        }

        // 在 1 小时 0 分钟的时候, 显示 1 小时, 不显示 0 分钟
        val minFmt = if (duration.toHours() > 0L && durationMin == 0L) {
            ""
        } else {
            String.format("%d分钟", durationMin)
        }

        val diffStr = hourFmt + minFmt

        if (diffStr != "") {
            resultFmt = "用药间隔约$diffStr"
        } else {
            resultFmt = ""
        }

        return resultFmt
    }
}