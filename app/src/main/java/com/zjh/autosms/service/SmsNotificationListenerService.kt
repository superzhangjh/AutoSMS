package com.zjh.autosms.service

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.zjh.autosms.utils.OSUtils
import com.zjh.autosms.utils.VerificationCodeUtils

class SmsNotificationListenerService : NotificationListenerService() {
    companion object {
        private val TAG = SmsNotificationListenerService::class.java.simpleName
        val SMS = OSUtils.getPackageName(OSUtils.APP_SMS)

        const val EXTRA_NLS_ACTION = "nls_action"
        const val EXTRA_VERIFICATION_CODE = "verification_code"
        const val EXTRA_NOTIFICATION_KEY = "notification_id"

        const val ACTION_CODE = 1
        const val ACTION_FILL_FINISH = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.extras?.getInt(EXTRA_NLS_ACTION)) {
            ACTION_FILL_FINISH -> {
                val key = intent.extras?.getString(EXTRA_NOTIFICATION_KEY)
                cancelNotification(key)
            }
        }
        return START_STICKY
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.d(TAG, "onNotificationPosted 包名:${sbn.packageName}")
        if (sbn.packageName == SMS) {
            sendCodeToABS(sbn)
        }
    }

    private fun sendCodeToABS(sbn: StatusBarNotification) {
        val notificationText = sbn.notification.extras.get(Notification.EXTRA_TEXT).toString()
        Log.d(TAG, "短信通知: $notificationText")
        VerificationCodeUtils.matchVerificationCode(notificationText)?.let {
            //将识别到的验证码发送给无障碍服务处理
            val intent = Intent().setComponent(ComponentName(packageName, SmsAccessibilityService::class.java.canonicalName!!))
            val bundle = Bundle()
            bundle.putInt(EXTRA_NLS_ACTION, ACTION_CODE)
            bundle.putString(EXTRA_VERIFICATION_CODE, it)
            bundle.putString(EXTRA_NOTIFICATION_KEY, sbn.key)
            intent.putExtras(bundle)
            startService(intent)
        }
    }
}