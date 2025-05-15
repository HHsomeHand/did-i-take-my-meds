package com.github.hhsomehand.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.github.hhsomehand.MyApplication
import com.github.hhsomehand.constant.PrefsConst
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val TAG = "rememberSharedState"

fun <T> sharedState(
    key: String,
    defaultValue: T,
    context: Context = MyApplication.instance.applicationContext
): SharedState<T> {
    val prefs = context.getSharedPreferences(PrefsConst.appStoreName, Context.MODE_PRIVATE)

    @Suppress("UNCHECKED_CAST")
    val state = mutableStateOf(
        when (defaultValue) {
            is Long -> prefs.getLong(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is String -> prefs.getString(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            else -> throw Exception("sharedState 只能记录基本数据类型")
        }
    )

    return object : SharedState<T> {
        override fun getValue(thisRef: Nothing?, property: KProperty<*>): T {
            return state.value
        }

        override val value: T
            get() = state.value

        override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T) {
            state.value = value
            with(prefs.edit()) {
                when (value) {
                    is Long -> putLong(key, value)
                    is Int -> putInt(key, value)
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    else -> throw Exception("sharedState 只能记录基本数据类型")
                }
                apply()
            }
        }
    }
}

interface SharedState<T> : State<T>, ReadWriteProperty<Nothing?, T>

@Composable
fun <T> rememberSharedState(
    key: String,
    defaultValue: T,
    context: Context = LocalContext.current
): SharedState<T> {
    return remember(key) {
        sharedState(key, defaultValue, context)
    }
}