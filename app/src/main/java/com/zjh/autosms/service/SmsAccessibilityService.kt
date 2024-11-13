package com.zjh.autosms.service

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.zjh.autosms.entity.LocalSetting
import com.zjh.autosms.event.BusEvent
import com.zjh.autosms.service.node.FocusedNodeInterceptor
import com.zjh.autosms.service.node.KeyboardInputInterceptor
import com.zjh.autosms.service.node.NumberNodeInterceptor
import com.zjh.autosms.utils.showToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * SmsAccessibilityService（ABS）与NotificationListenerService（NLS）的交互
 * (1)在接收到短信通知的时候，给NLS发送intent确保其启动
 * (2)NLS启动时开始处理验证码，检测到验证码时使用intent将数据传给ABS做自动填充服务
 * (3)ABS填充完毕时，使用intent将结果告诉NLS，NLS再将填充完毕的验证码通知清除
 */
class SmsAccessibilityService : AccessibilityService() {
    companion object {
        private val TAG = SmsAccessibilityService::class.java.simpleName
    }
    private var localSetting: LocalSetting? = null
    private val nodeInterceptors = listOf(
        FocusedNodeInterceptor(),
        NumberNodeInterceptor(this),
        KeyboardInputInterceptor(this)
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getIntExtra(SmsNotificationListenerService.EXTRA_NLS_ACTION, 0)
        when (action) {
            SmsNotificationListenerService.ACTION_CODE -> {
                val bundle = intent.extras
                val code = bundle?.getString(SmsNotificationListenerService.EXTRA_VERIFICATION_CODE) ?: ""
                val sbnKey = bundle?.getString(SmsNotificationListenerService.EXTRA_NOTIFICATION_KEY) ?: ""
                onMatcherVerificationCode(code, sbnKey)
            }
        }
        return START_STICKY
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        EventBus.getDefault().register(this)
        localSetting = LocalSetting.getDefault()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent type:${event.eventType} cls:${event.className} text:${event.text}")
        when (event.eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                Log.d(TAG, "接收通知 ${event.text}")
                if (SmsNotificationListenerService.SMS == event.packageName) {
                    startNLSService(null)
                }
            }
        }
    }

    private fun startNLSService(extras: Bundle?) {
        val intent = Intent().setClassName(packageName, SmsNotificationListenerService::class.java.canonicalName!!)
        extras?.let {
            intent.putExtras(it)
        }
        startService(intent)
    }

    override fun onInterrupt() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onBusEvent(event: BusEvent) {
        Log.d(TAG, "onBusEvent: $event")
        when (event.what) {
            BusEvent.UPDATE_SETTING -> {
                localSetting = event.extra as LocalSetting
            }
            //结束输入时切换为默认的输入法
            BusEvent.SERVICE_FINISH_INPUT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    resetInputMethod()
                }
            }
        }
    }

    private fun onMatcherVerificationCode(code: String, sbnKey: String) {
        if (localSetting?.addClipping == true) {
            addCodeToClipping(code)
        }
        rootInActiveWindow?.let {
            for (nodeInterceptor in nodeInterceptors) {
                if (nodeInterceptor.onAccept(code, it)) {
                    if (localSetting?.removeClippingAlterUse == true) {
                        removeClippingAlterUse()
                    }
                    val extras = Bundle()
                    extras.putInt(SmsNotificationListenerService.EXTRA_NLS_ACTION, SmsNotificationListenerService.ACTION_FILL_FINISH)
                    extras.putString(SmsNotificationListenerService.EXTRA_NOTIFICATION_KEY, sbnKey)
                    startNLSService(extras)
                    break
                }
            }
        }
    }

    private fun addCodeToClipping(code: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Verification Code", code))
            if (localSetting?.toastAddClipping == true) {
                showToast("已复制验证码：$code")
            }
        }
    }

    private fun removeClippingAlterUse() {
        (getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.clearPrimaryClip()
            } else {
                it.setPrimaryClip(ClipData.newPlainText(null, null))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun resetInputMethod() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputMethodInfo in imm.enabledInputMethodList) {
            //记录默认id，填充完需要复原回去
            if (inputMethodInfo.isDefaultResourceId > 0) {
                val success = softKeyboardController.switchToInputMethod(inputMethodInfo.id)
                Log.d(TAG, "切换输入法：${inputMethodInfo.component.className} success:$success")
                break
            }
        }
    }

//    private fun fillVerificationCode(code: String) {
//        rootInActiveWindow?.let {
//            val focusedNode = findFocusedNode(it)
//            if (focusedNode != null) {
//                Log.d(TAG, "当前获得焦点的控件的文本：${focusedNode.text}")
//                //todo:适配无焦点时的情况
//                //todo:适配多输入框的情况（哈啰）
//                //todo:适配WebView情况
//                val arguments = Bundle()
//                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, code)
//                focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
//            }
//        }
//    }
}