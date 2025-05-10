package com.github.hhsomehand

import android.app.Application

/**
 * AndroidManifest.xml
 *
 *     <application
 *         android:name=".MyApplication" <- 这里
 */
class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this // 在 Application 创建时保存实例
    }
}