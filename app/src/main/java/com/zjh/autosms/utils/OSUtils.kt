package com.zjh.autosms.utils

import android.os.Build
import android.util.Log

object OSUtils {
    const val APP_SMS = "SMS"

    fun getPackageName(app: String): String {
        Log.d("系统", Build.DEVICE)
        return when (app) {
            APP_SMS -> "com.android.mms"
            else -> "com.android.sms"
        }
    }
}