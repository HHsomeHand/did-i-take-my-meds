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
import kotlinx.coroutines.withContext
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
}