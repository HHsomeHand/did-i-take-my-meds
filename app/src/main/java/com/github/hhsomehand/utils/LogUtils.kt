package com.github.hhsomehand.utils

import android.util.Log

object LogUtils {
    private const val VERBOSE = 1

    private const val DEBUG = 2

    private const val INFO = 3

    private const val WARN = 4

    private const val ERROR = 5

    // TODO: 发布时, level 修改为 ERROR
    private var level = VERBOSE

    // tag 的前缀, 这里使用 App 名作为前缀, 下面调用 v b 之类的函数, tag 使用类名
    private const val tagPrex = "DidITakeMyMeds."

    fun v(tag: String, msg: String) {
        if (level <= VERBOSE) {
            Log.v(tagPrex + tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= DEBUG) {
            Log.d(tagPrex + tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (level <= INFO) {
            Log.i(tagPrex + tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (level <= WARN) {
            Log.w(tagPrex + tag, msg)
        }

    }

    fun e(tag: String, msg: String) {
        if (level <= ERROR) {
            Log.e(tagPrex + tag, msg)
        }
    }

}