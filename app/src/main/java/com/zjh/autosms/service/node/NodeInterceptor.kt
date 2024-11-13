package com.zjh.autosms.service.node

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/**
 * node拦截器
 */
abstract class NodeInterceptor {
    /**
     * 接收事件处理
     * @return 是否消费
     */
    abstract fun onAccept(code: String, rootNode: AccessibilityNodeInfo): Boolean

    fun fillCode(code: String, node: AccessibilityNodeInfo) {
        if (node.text?.toString()?.trim() == code) return
        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, code)
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }
}