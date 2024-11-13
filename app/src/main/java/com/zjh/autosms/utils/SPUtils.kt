package com.zjh.autosms.utils

import android.content.Context
import android.content.SharedPreferences
import com.zjh.autosms.app.App.Companion.getApp

class SPUtils private constructor(context: Context) {
    companion object {
        const val KEY_LOCAL_SETTING = "local_setting"
        const val KEY_AUTO_FILL_CODE = "auto_fill_code"

        private var instance: SPUtils? = null

        fun getInstance(): SPUtils {
            if (instance == null) {
                synchronized(SPUtils::class) {
                    if (instance == null) {
                        instance = SPUtils(getApp())
                    }
                }
            }
            return instance!!
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    // 存储字符串
    fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    // 读取字符串
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // 存储整数
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    // 读取整数
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // 存储布尔值
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    // 读取布尔值
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // 删除指定键
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    // 清空所有数据
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

