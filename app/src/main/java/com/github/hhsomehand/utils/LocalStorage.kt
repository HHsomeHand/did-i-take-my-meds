package com.github.hhsomehand.utils

import android.content.Context
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.constant.PrefsConst
import com.github.hhsomehand.model.LocalStorageRecord

object LocalStorage {
    fun <T> get(
        record: LocalStorageRecord<T>,
        context: Context = MyApplication.instance.applicationContext
    ): T {
        return get(context, record.key, record.value)
    }

    fun <T> get(
        key: String,
        defaultValue: T,
        context: Context = MyApplication.instance.applicationContext
    ): T {
        var prefs = context.getSharedPreferences(PrefsConst.appStoreName, Context.MODE_PRIVATE)

        return when (defaultValue) {
            is Long -> prefs.getLong(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is String -> prefs.getString(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            else -> throw Exception("PrefsUtils.get 只能记录基本数据类型")
        }
    }

    /**
     * 遗弃的函数, 不要使用
     */
    fun <T> get(context: Context, key: String, defaultValue: T): T {
        return get(key, defaultValue, context)
    }
}