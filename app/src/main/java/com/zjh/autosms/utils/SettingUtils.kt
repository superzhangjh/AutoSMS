package com.zjh.autosms.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.INPUT_METHOD_SERVICE
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationManagerCompat
import com.zjh.autosms.service.AutoFillInputMethodService


object SettingUtils {
    /**
     * 判断无障碍服务是否开启
     */
     fun <T : AccessibilityService> isAccessibilitySettingsOn(context: Context, serviceCls: Class<T>): Boolean {
        var accessibilityEnabled = 0
        val serviceName = context.packageName + "/" + serviceCls.getCanonicalName()
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(serviceName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun toAccessibilitySetting(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    fun isNotificationListenerServiceEnabled(context: Context): Boolean {
        val packageNames = NotificationManagerCompat.getEnabledListenerPackages(context)
        return packageNames.contains(context.packageName)
    }

    fun toNotificationListenerSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
    }

    /**
     * 检查应用是否被电池优化
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) == true
    }

    /**
     * 电池设置
     */
    fun toBatterySetting(context: Context) {
        context.startActivity(Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }

    /**
     * 输入法是否启用
     */
    fun isInputMethodServiceOn(context: Context): Boolean {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputMethodInfo in imm.enabledInputMethodList) {
            if (inputMethodInfo.component.className == AutoFillInputMethodService::class.java.canonicalName) {
                return true
            }
        }
        return false
    }

    fun openInputMethodSettings(context: Context) {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
        context.startActivity(intent)
    }
}