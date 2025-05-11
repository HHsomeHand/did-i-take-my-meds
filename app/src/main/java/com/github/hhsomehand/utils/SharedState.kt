package com.github.hhsomehand.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.github.hhsomehand.MyApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val TAG = "rememberSharedState"

class SharedState<T>(
    private val key: String,
    private val defaultValue: T,
    private val context: Context = MyApplication.instance.applicationContext
) {
    private val prefs = context.getSharedPreferences("countdown_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    @Suppress("UNCHECKED_CAST")
    private var _value: T by mutableStateOf(
        when (defaultValue) {
            is Long -> prefs.getLong(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is String -> prefs.getString(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            else -> {
                val json = prefs.getString(key, null)
                if (json != null) {
                    try {
                        val type = object : TypeToken<T>() {}.type
                        gson.fromJson(json, type) as T
                    } catch (e: Exception) {
                        defaultValue
                    }
                } else {
                    defaultValue
                }
            }
        }
    )

    var value: T
        get() = _value
        set(newValue) {
            _value = newValue
            with(prefs.edit()) {
                when (newValue) {
                    is Long -> putLong(key, newValue)
                    is Int -> putInt(key, newValue)
                    is String -> putString(key, newValue)
                    is Boolean -> putBoolean(key, newValue)
                    is Float -> putFloat(key, newValue)
                    else -> {
                        try {
                            val json = gson.toJson(newValue)
                            putString(key, json)
                        } catch (e: Exception) {
                            LogUtils.e(TAG, "Failed to serialize value to JSON")
                        }
                    }
                }
                apply()
            }
        }
}

@Composable
fun <T> rememberSharedState(
    key: String,
    defaultValue: T,
    context: Context = LocalContext.current
): SharedState<T> {
    return remember(key) {
        SharedState(key, defaultValue, context)
    }
}