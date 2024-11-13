package com.zjh.autosms.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.zjh.autosms.app.App.Companion.getApp

private val handler by lazy { Handler(Looper.getMainLooper()) }

fun showToast(content: String) {
    showToast(getApp(), content)
}

fun showToast(context: Context, content: String) {
    handler.post {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }
}