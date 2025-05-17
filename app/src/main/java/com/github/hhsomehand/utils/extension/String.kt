package com.github.hhsomehand.utils.extension

import android.content.Context
import android.widget.Toast
import com.github.hhsomehand.MyApplication


fun String.showToast(
    context: Context = MyApplication.instance.applicationContext,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(context, this, duration).show()
}